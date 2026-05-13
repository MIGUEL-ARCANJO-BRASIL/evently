package fametro.edu.br.evently.event.controller;

import fametro.edu.br.evently.event.dto.*;
import fametro.edu.br.evently.event.service.EventSubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class EventSubscriptionController {
    private final EventSubscriptionService subscriptionService;

    @PostMapping("/join-event")
    public String joinEvent(@Valid @ModelAttribute("subscriptionForm") JoinEventFormDTO form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        String redirectUrl = form.getEventId() != null ? "redirect:/" + form.getEventId() : "redirect:/";
        String checkoutRedirectUrl = redirectUrl;

        if (form.getEventId() != null && form.getSelectedTickets() != null && !form.getSelectedTickets().isBlank()) {
            checkoutRedirectUrl = "redirect:/" + form.getEventId() + "/checkout?selectedTickets="
                    + form.getSelectedTickets();
        }

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());

            redirectAttributes.addFlashAttribute("erroInscricao",
                    "Existem erros no formulário: " + String.join(", ", errors));
            redirectAttributes.addFlashAttribute("subscriptionForm", form);
            return checkoutRedirectUrl;
        }

        try {
            EventSubscriptionResponseDTO subscription = this.subscriptionService.joinEvent(form);
            redirectAttributes.addFlashAttribute("sucessoInscricao", "Inscrição realizada com sucesso!");
            return "redirect:/subscription/" + subscription.getId() + "/summary";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erroInscricao", e.getMessage());
            redirectAttributes.addFlashAttribute("subscriptionForm", form);
            return checkoutRedirectUrl;
        }
    }

    @GetMapping("/{id}/summary")
    public String showSummary(@PathVariable UUID id, Model model) {
        EventSubscriptionSummaryDTO summary = subscriptionService.getSummarySubscription(id);
        model.addAttribute("summary", summary);
        return "events/subscription-summary";
    }

    @GetMapping("/{id}/qrcode")
    @ResponseBody
    public ResponseEntity<byte[]> getQRCode(@PathVariable UUID id) {
        try {
            byte[] qrCode = subscriptionService.generateQRCodeImage(id.toString(), 200, 200);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCode);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/ticket/pdf")
    public ResponseEntity<byte[]> downloadTicket(@PathVariable UUID id) {
        try {
            byte[] pdf = subscriptionService.generateTicketPDF(id);
            String filename = "ticket-" + id + ".pdf";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
