package fametro.edu.br.evently.event.controller;

import fametro.edu.br.evently.event.dto.JoinEventFormDTO;
import fametro.edu.br.evently.event.service.EventSubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/events/subscription")
@RequiredArgsConstructor
public class EventSubscriptionController {
    private final EventSubscriptionService subscriptionService;

    @PostMapping("/join-event")
    public String joinEvent(@Valid @ModelAttribute("subscriptionForm") JoinEventFormDTO form,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        String redirectUrl = form.getEventId() != null ? "redirect:/events/" + form.getEventId() : "redirect:/events";
        String checkoutRedirectUrl = redirectUrl;

        if (form.getEventId() != null && form.getSelectedTickets() != null && !form.getSelectedTickets().isBlank()) {
            checkoutRedirectUrl = "redirect:/events/" + form.getEventId() + "/checkout?selectedTickets=" + form.getSelectedTickets();
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("erroInscricao", "Preencha corretamente os campos obrigatórios.");
            return checkoutRedirectUrl;
        }

        try {
            this.subscriptionService.joinEvent(form);
            redirectAttributes.addFlashAttribute("sucessoInscricao", "Inscrição realizada com sucesso!");
            return redirectUrl;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erroInscricao", e.getMessage());
            return checkoutRedirectUrl;
        }
    }
}
