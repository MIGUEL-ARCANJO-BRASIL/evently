package fametro.edu.br.evently.event.controller;

import fametro.edu.br.evently.event.dto.EventFormDTO;
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

import java.util.List;
import java.util.UUID;

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
                       Model model) {
        List<Event> lista = eventService.findFiltered(category, query);
        model.addAttribute("events", lista);
        model.addAttribute("categories", categoryService.findAll());
        return "events/home";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("event", eventService.findById(id));
        return "events/detail"; // Sem a palavra 'templates/'
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
        form.setLocation(event.getLocation());
        form.setTotalSlots(event.getTotalSlots());
        form.setValue(event.getValue());
        form.setRegistrationDeadline(event.getRegistrationDeadline());
        if (event.getCategory() != null) {
            form.setCategoryId(event.getCategory().getId());
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
            log.info("Sem imagem nova. isEmpty={}, size={}, name={}",
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

}