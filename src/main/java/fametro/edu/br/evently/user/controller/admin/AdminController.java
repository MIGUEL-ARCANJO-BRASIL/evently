package fametro.edu.br.evently.user.controller.admin;

import fametro.edu.br.evently.event.service.EventService;
import fametro.edu.br.evently.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import fametro.edu.br.evently.event.service.CategoryService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EventService eventService;
    private final CategoryService categoryService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public String dashboard(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("usuario", user.getName());
        return "admin/dashboard";
    }

    @GetMapping("/my-events")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public String events(@RequestParam(required = false) String category,
                         @RequestParam(required = false) String query,
                         Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("usuario", user.getName());
        model.addAttribute("events", eventService.findFilteredByOrganizer(user.getId(), category, query));
        model.addAttribute("categories", categoryService.findAll());
        return "admin/my-events";
    }
}