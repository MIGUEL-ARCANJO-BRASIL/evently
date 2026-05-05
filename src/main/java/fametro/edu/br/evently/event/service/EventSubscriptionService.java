package fametro.edu.br.evently.event.service;

import fametro.edu.br.evently.event.dto.EventSubscriptionResponseDTO;
import fametro.edu.br.evently.event.dto.EventSubscriptionSummaryDTO;
import fametro.edu.br.evently.event.dto.JoinEventFormDTO;
import fametro.edu.br.evently.event.dto.MyTicketsDTO;
import fametro.edu.br.evently.event.enums.PaymentMethod;
import fametro.edu.br.evently.event.enums.TransactionStatus;
import fametro.edu.br.evently.event.model.*;
import fametro.edu.br.evently.event.repository.EventRepository;
import fametro.edu.br.evently.event.repository.EventSubscriptionRepository;
import fametro.edu.br.evently.event.repository.EventTransactionRepository;
import fametro.edu.br.evently.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;
import java.util.Objects;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventSubscriptionService {
    private final EventSubscriptionRepository subscriptionRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventTransactionRepository transactionRepository;

    @Transactional
    public EventSubscriptionResponseDTO joinEvent(JoinEventFormDTO dto) {
        var event = this.eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado"));

        validateUser(dto.getEventId(), dto);

        if (event.getAvailableSlots() == 0) {
            throw new RuntimeException("Não há vagas disponíveis para este evento.");
        }

        double totalPaid = calculatePaidValue(event, dto.getSelectedTickets());

        PaymentMethod finalPaymentMethod = dto.getPaymentMethod();
        if (totalPaid <= 0) {
            finalPaymentMethod = PaymentMethod.GRATUITO;
        }

        PaymentCard paymentCard = null;
        if (finalPaymentMethod == PaymentMethod.CARTAO_CREDITO ||
                finalPaymentMethod == PaymentMethod.CARTAO_DEBITO) {
            paymentCard = PaymentCard.builder()
                    .cardBrand(dto.getCardBrand())
                    .paymentToken(dto.getPaymentToken())
                    .lastFourDigits(dto.getLastFourDigits())
                    .build();
        }

        var eventSubscription = EventSubscription
                .builder()
                .event(event)
                .userEmail(dto.getUserEmail())
                .userName(dto.getUserName())
                .userSecondName(dto.getUserSecondName())
                .userCpf(dto.getUserCpf())
                .userPhone(dto.getUserPhone())
                .userSecondPhone(dto.getUserSecondPhone())
                .userCity(dto.getUserCity())
                .paymentMethod(finalPaymentMethod)
                .paidValue(totalPaid)
                .subscriptionDate(LocalDateTime.now())
                .userAgeRange(dto.getUserAgeRange())
                .paymentCard(paymentCard)
                .checkedIn(false)
                .build();

        List<SubscriptionItem> items = buildSubscriptionItems(eventSubscription, event, dto.getSelectedTickets());
        eventSubscription.setItems(items);

        event.setAvailableSlots(event.getAvailableSlots() - 1);

        this.eventRepository.save(event);

        var subscriptionSaved = this.subscriptionRepository.save(eventSubscription);

        var eventTransaction = EventTransaction
                .builder()
                .amount(BigDecimal.valueOf(totalPaid))
                .paymentMethod(finalPaymentMethod)
                .status(totalPaid > 0 ? TransactionStatus.PENDENTE : TransactionStatus.PAGO)
                .processedAt(LocalDateTime.now())
                .subscription(subscriptionSaved)
                .build();
        this.transactionRepository.save(eventTransaction);

        return EventSubscriptionResponseDTO.builder()
                .id(subscriptionSaved.getId())
                .build();
    }

    private void validateUser(UUID eventId, JoinEventFormDTO dto) {
        log.info("Validando inscrição para evento {}: email={}, cpf={}, phone = {}", eventId, dto.getUserEmail(),
                dto.getUserCpf(), dto.getUserPhone());

        this.subscriptionRepository.findByEvent_IdAndUserEmail(eventId, dto.getUserEmail()).ifPresent(e -> {
            throw new RuntimeException("Já existe uma inscrição para este email neste evento.");
        });

        this.userRepository.findByEmail(dto.getUserEmail()).ifPresent(e -> {
            if (!e.getCpf().equals(dto.getUserCpf())) {
                throw new RuntimeException("O email informado já está associado a um CPF diferente.");
            }
        });

        this.userRepository.findByCpf(dto.getUserCpf()).ifPresent(e -> {
            if (!e.getEmail().equals(dto.getUserEmail())) {
                throw new RuntimeException("O CPF informado já está associado a um email diferente.");
            }
        });

        this.subscriptionRepository.findByEvent_IdAndUserCpf(eventId, dto.getUserCpf()).ifPresent(e -> {
            throw new RuntimeException("Já existe uma inscrição para este CPF neste evento.");
        });

    }

    private double calculatePaidValue(Event event, String selectedTickets) {
        if (selectedTickets == null || selectedTickets.isBlank()) {
            return 0.0;
        }

        Map<UUID, Double> priceByTicketId = event.getTickets().stream()
                .collect(Collectors.toMap(
                        EventTicket::getId,
                        ticket -> ticket.getValue() != null ? ticket.getValue() : 0.0));

        return Arrays.stream(selectedTickets.split(","))
                .map(String::trim)
                .filter(part -> !part.isBlank() && part.contains(":"))
                .map(part -> part.split(":"))
                .filter(parts -> parts.length == 2)
                .map(parts -> Map.entry(parseUuid(parts[0]), parseQuantity(parts[1])))
                .filter(entry -> entry.getKey() != null && entry.getValue() > 0)
                .mapToDouble(entry -> priceByTicketId.getOrDefault(entry.getKey(), 0.0) * entry.getValue())
                .sum();
    }

    private List<SubscriptionItem> buildSubscriptionItems(
            EventSubscription subscription, Event event, String selectedTickets) {
        if (selectedTickets == null || selectedTickets.isBlank()) {
            return List.of();
        }

        Map<UUID, EventTicket> ticketMap = event.getTickets().stream()
                .collect(Collectors.toMap(EventTicket::getId, t -> t));

        return Arrays.stream(selectedTickets.split(","))
                .map(String::trim)
                .filter(part -> !part.isBlank() && part.contains(":"))
                .map(part -> part.split(":"))
                .filter(parts -> parts.length == 2)
                .map(parts -> {
                    UUID id = parseUuid(parts[0]);
                    int qty = parseQuantity(parts[1]);
                    var ticket = ticketMap.get(id);
                    if (ticket != null && qty > 0) {
                        if (ticket.getExpirationDate() != null &&
                                ticket.getExpirationDate().isBefore(LocalDate.now())) {
                            throw new RuntimeException("O lote '" + ticket.getName() + "' já está encerrado.");
                        }
                        return SubscriptionItem.builder()
                                .subscription(subscription)
                                .ticket(ticket)
                                .quantity(qty)
                                .unitPrice(ticket.getValue())
                                .build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private UUID parseUuid(String value) {
        try {
            return UUID.fromString(value.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private int parseQuantity(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception ignored) {
            return 0;
        }
    }

    public EventSubscriptionSummaryDTO getSummarySubscription(UUID subscriptionId) {
        var subscription = this.subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Inscrição não encontrada"));

        var transaction = this.transactionRepository.findBySubscription_Id(subscriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Transação não encontrada"));

        var event = subscription.getEvent();

        List<String> ticketsName = subscription.getItems().stream().map(
                item -> item.getTicket().getName() + " (x" + item.getQuantity() + ")").toList();

        return EventSubscriptionSummaryDTO.builder()
                .eventTitle(event.getTitle())
                .eventDate(event.getEventDate())
                .eventCity(event.getEventLocalization().getCity())
                .eventState(event.getEventLocalization().getState())
                .userName(subscription.getUserName() + " " + subscription.getUserSecondName())
                .ticketsName(ticketsName)
                .idSubscription(subscription.getId())
                .idEventTransaction(transaction.getId())
                .paymentMethod(subscription.getPaymentMethod())
                .purchaseDate(subscription.getSubscriptionDate())
                .userEmail(subscription.getUserEmail())
                .build();

    }

    public List<MyTicketsDTO> listAllMyTickets(String userEmail) {

        var subscriptions = this.subscriptionRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Nenhuma inscrição encontrada para este email"));

        return subscriptions.stream().map(MyTicketsDTO::toDTO).toList();
    }

    public byte[] generateQRCodeImage(String text, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    public byte[] generateTicketPDF(UUID subscriptionId) throws Exception {
        var subscription = this.subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Inscrição não encontrada"));

        var event = subscription.getEvent();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, baos);

        document.open();

        // Custom styling for PDF to match Image 2
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font categoryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.RED);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[] { 3, 1 });

        // Left Side
        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setPadding(20);

        String categoryName = event.getCategory() != null ? event.getCategory().getName() : "EVENTO";
        leftCell.addElement(new Paragraph(categoryName.toUpperCase(), categoryFont));
        leftCell.addElement(new Paragraph(event.getTitle(), titleFont));

        PdfPTable detailsTable = new PdfPTable(2);
        detailsTable.setWidthPercentage(100);
        detailsTable.setSpacingBefore(20);

        var formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        addDetailToTable(detailsTable, "DATA E HORA", event.getEventDate().format(formatter), labelFont, valueFont);
        addDetailToTable(detailsTable, "LOCALIZAÇÃO",
                event.getEventLocalization().getCity() + ", " + event.getEventLocalization().getState(), labelFont,
                valueFont);
        addDetailToTable(detailsTable, "PARTICIPANTE",
                subscription.getUserName() + " " + subscription.getUserSecondName(), labelFont, valueFont);

        String ticketTypes = subscription.getItems().stream()
                .map(item -> item.getTicket().getName())
                .collect(Collectors.joining(", "));
        addDetailToTable(detailsTable, "TIPO DE INGRESSO", ticketTypes, labelFont, valueFont);

        leftCell.addElement(detailsTable);
        table.addCell(leftCell);

        // Right Side (QR Code)
        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setBackgroundColor(new Color(245, 245, 255));
        rightCell.setPadding(20);
        rightCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        rightCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        byte[] qrBytes = generateQRCodeImage(subscription.getId().toString(), 150, 150);
        Image qrImage = Image.getInstance(qrBytes);
        qrImage.setAlignment(Image.ALIGN_CENTER);
        rightCell.addElement(qrImage);

        Paragraph scanText = new Paragraph("SCAN AT ENTRANCE", labelFont);
        scanText.setAlignment(Element.ALIGN_CENTER);
        rightCell.addElement(scanText);

        table.addCell(rightCell);

        document.add(table);
        document.close();

        return baos.toByteArray();
    }

    private void addDetailToTable(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(10);
        cell.addElement(new Paragraph(label, labelFont));
        cell.addElement(new Paragraph(value, valueFont));
        table.addCell(cell);
    }
}