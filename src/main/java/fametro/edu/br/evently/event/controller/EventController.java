package fametro.edu.br.evently.event.controller;

import fametro.edu.br.evently.event.dto.EventFormDTO;
import fametro.edu.br.evently.event.model.Event;
import fametro.edu.br.evently.event.repository.CategoryRepository;
import fametro.edu.br.evently.event.service.EventService;
import fametro.edu.br.evently.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    private final CategoryRepository categoryRepository;

    @GetMapping
    public String list(Model model) {
        List<Event> lista = eventService.findAllActive();
        System.out.println("Quantidade de eventos ativos encontrados: " + lista.size());
        model.addAttribute("events", lista);
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
        model.addAttribute("categories", categoryRepository.findAll());
        return "events/form"; // Sem a palavra 'templates/'
    }

    @PostMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZADOR')")
    public String save(@Valid @ModelAttribute("eventForm") EventFormDTO form,
                       BindingResult result,
                       Model model,
                       Authentication authentication,
                       RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            return "templates/events/form";
        }

        User user = (User) authentication.getPrincipal();
        eventService.save(form, user);
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
        form.setEventDate(event.getEventDate()); // Verifique se o nome do campo no DTO bate
        form.setLocation(event.getLocation());
        form.setTotalSlots(event.getTotalSlots());
        if (event.getCategory() != null) {
            form.setCategoryId(event.getCategory().getId());
        }

        model.addAttribute("event", event); // Para usar o ID no th:action
        model.addAttribute("eventForm", form); // Agora o formulário já nasce preenchido
        model.addAttribute("categories", categoryRepository.findAll());

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
            model.addAttribute("categories", categoryRepository.findAll());
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
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        eventService.archive(id);
        redirectAttributes.addFlashAttribute("sucesso", "Evento Arquivado!");
        return "redirect:/events";
    }
}