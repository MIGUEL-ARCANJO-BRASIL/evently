package fametro.edu.br.evently.event.controller;

import fametro.edu.br.evently.event.dto.EventFormDTO;
import fametro.edu.br.evently.event.dto.JoinEventFormDTO;
import fametro.edu.br.evently.event.enums.AgeRange;
import fametro.edu.br.evently.event.enums.PaymentMethod;
import fametro.edu.br.evently.event.model.Event;
import fametro.edu.br.evently.event.service.CategoryService;
import fametro.edu.br.evently.event.service.EventService;
import fametro.edu.br.evently.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;
    private final CategoryService categoryService;

    @GetMapping
    public String list(@RequestParam(required = false) String category, 
                       @RequestParam(required = false) String query, 
                       @RequestParam(required = false) String date,
                       @RequestParam(required = false) String price,
                       @RequestParam(required = false) String city,
                       Model model) {
        List<Event> lista = eventService.findFiltered(category, query, date, price, city);
        model.addAttribute("events", lista);
        model.addAttribute("categories", categoryService.findAll());
        return "events/home";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id,
                         @AuthenticationPrincipal User user,
                         Model model) {
        model.addAttribute("event", eventService.findById(id));
        return "events/detail"; // Sem a palavra 'templates/'
    }

    @PostMapping("/{id}/checkout")
    public String checkout(@PathVariable UUID id,
                           @RequestParam String selectedTickets,
                           @AuthenticationPrincipal User user,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        return renderCheckout(id, selectedTickets, user, redirectAttributes, model);
    }

    @GetMapping("/{id}/checkout")
    public String checkoutPage(@PathVariable UUID id,
                               @RequestParam String selectedTickets,
                               @AuthenticationPrincipal User user,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        return renderCheckout(id, selectedTickets, user, redirectAttributes, model);
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("eventForm", new EventFormDTO());
        model.addAttribute("categories", categoryService.findAll());
        return "events/create-event"; // Sem a palavra 'templates/'
    }

    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZADOR')")
    public String createEvent(@Valid @ModelAttribute("eventForm") EventFormDTO form,
                              BindingResult result,
                              @AuthenticationPrincipal User organizer,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            return "events/create-event";
        }

        eventService.create(form, organizer);

        redirectAttributes.addFlashAttribute("sucesso", "Evento criado com sucesso!");
        return "redirect:/events";
    }


    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZADOR')")
    public String editForm(@PathVariable UUID id, Model model) {
        Event event = eventService.findById(id);

        // Criar o DTO e preencher com os dados existentes
        EventFormDTO form = new EventFormDTO();
        form.setTitle(event.getTitle());
        form.setDescription(event.getDescription());
        form.setEventDate(event.getEventDate());
        form.setTotalSlots(event.getTotalSlots());
        form.setRegistrationDeadline(event.getRegistrationDeadline());

        if (event.getEventLocalization() != null) {
            form.setCep(event.getEventLocalization().getCep());
            form.setAddress(event.getEventLocalization().getAddress());
            form.setComplement(event.getEventLocalization().getComplement());
            form.setNumber(event.getEventLocalization().getNumber());
            form.setCity(event.getEventLocalization().getCity());
            form.setState(event.getEventLocalization().getState());
            form.setNeighborhood(event.getEventLocalization().getNeighborhood());
        }

        if (event.getCategory() != null) {
            form.setCategoryId(event.getCategory().getId());
        }

        if (event.getTickets() != null && !event.getTickets().isEmpty()) {
            form.setEventTicket(event.getTickets().stream()
                .map(t -> new fametro.edu.br.evently.event.dto.EventTicketDTO(
                    event.getId(), 
                    t.getName(), 
                    t.getValue(), 
                    t.getExpirationDate(), 
                    t.getQuantity()))
                .toList());
        }

        model.addAttribute("event", event); // Para usar o ID no th:action
        model.addAttribute("eventForm", form); // Agora o formulário já nasce preenchido
        model.addAttribute("categories", categoryService.findAll());

        return "events/form-edit";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZADOR')")
    public String update(@PathVariable UUID id,
                         @Valid @ModelAttribute("eventForm") EventFormDTO form,
                         BindingResult result,
                         @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("event", eventService.findById(id));
            return "events/form-edit";
        }

        // Injeta a imagem manualmente no form antes de passar pro service
        if (coverImage != null && !coverImage.isEmpty()) {
            log.info("Imagem recebida: {}, tamanho: {}", coverImage.getOriginalFilename(), coverImage.getSize());
            form.setCoverImage(coverImage);
        } else {
            log.info("Sem imagem nova. isEmpty={}, size={}, userName={}",
                    coverImage.isEmpty(), coverImage.getSize(), coverImage.getOriginalFilename());
        }

        eventService.update(id, form);
        redirectAttributes.addFlashAttribute("sucesso", "Evento atualizado!");
        return "redirect:/events";
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZADOR')")
    public String archive(@PathVariable UUID id, @RequestParam fametro.edu.br.evently.event.enums.EventStatus status, RedirectAttributes redirectAttributes) {
        eventService.archiveOrUnarchive(id, status);
        String msg = status == fametro.edu.br.evently.event.enums.EventStatus.ARQUIVADO ? "Evento Arquivado!" : "Evento Desarquivado!";
        redirectAttributes.addFlashAttribute("sucesso", msg);
        return "redirect:/admin/my-events";
    }

    private JoinEventFormDTO buildSubscriptionForm(UUID eventId, User user) {
        String userName = "";
        String userSecondName = "";

        if (user != null && user.getName() != null) {
            String[] nameParts = user.getName().trim().split("\\s+", 2);
            userName = nameParts[0];
            if (nameParts.length > 1) {
                userSecondName = nameParts[1];
            }
        }

        return JoinEventFormDTO.builder()
                .eventId(eventId)
                .userName(userName)
                .userSecondName(userSecondName)
                .userCpf(user != null ? user.getCpf() : "")
                .userEmail(user != null ? user.getEmail() : "")
                .userPhone(user != null ? user.getPhone() : "")
                .userSecondPhone("")
                .userCity("")
                .build();
    }

    private String renderCheckout(UUID eventId,
                                  String selectedTickets,
                                  User user,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        Event event = eventService.findById(eventId);
        List<CheckoutItem> checkoutItems = buildCheckoutItems(event, selectedTickets);

        if (checkoutItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("erroInscricao", "Selecione ao menos um ingresso.");
            return "redirect:/events/" + eventId;
        }

        double totalAmount = checkoutItems.stream()
                .mapToDouble(item -> item.unitPrice() * item.quantity())
                .sum();

        JoinEventFormDTO subscriptionForm = buildSubscriptionForm(eventId, user);
        subscriptionForm.setSelectedTickets(selectedTickets);

        model.addAttribute("event", event);
        model.addAttribute("checkoutItems", checkoutItems);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("subscriptionForm", subscriptionForm);
        model.addAttribute("ageRanges", AgeRange.values());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        return "events/checkout";
    }

    private List<CheckoutItem> buildCheckoutItems(Event event, String selectedTickets) {
        if (selectedTickets == null || selectedTickets.isBlank()) {
            return List.of();
        }

        Map<UUID, Integer> quantityByTicket = Arrays.stream(selectedTickets.split(","))
                .map(String::trim)
                .filter(part -> !part.isBlank() && part.contains(":"))
                .map(part -> part.split(":"))
                .filter(parts -> parts.length == 2)
                .map(parts -> Map.entry(parseUuid(parts[0]), parseQuantity(parts[1])))
                .filter(entry -> entry.getKey() != null && entry.getValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum));

        return event.getTickets().stream()
                .filter(ticket -> quantityByTicket.containsKey(ticket.getId()))
                .map(ticket -> new CheckoutItem(
                        ticket.getName(),
                        quantityByTicket.get(ticket.getId()),
                        ticket.getValue() != null ? ticket.getValue() : 0.0
                ))
                .toList();
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

    private record CheckoutItem(String ticketName, int quantity, double unitPrice) {}

}