package fametro.edu.br.evently.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal
            principal) {
        model.addAttribute("usuario", principal.getName());
        return "admin/dashboard"; // templates/admin/dashboard.html
    }
}