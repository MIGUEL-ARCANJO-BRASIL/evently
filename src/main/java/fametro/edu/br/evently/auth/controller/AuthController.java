package fametro.edu.br.evently.auth.controller;

import fametro.edu.br.evently.auth.dto.request.UserRegisterRequestDTO;
import fametro.edu.br.evently.auth.service.AuthService;
import fametro.edu.br.evently.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("usuarioForm", new UserRegisterRequestDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("usuarioForm") UserRegisterRequestDTO form,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "auth/register";
        }

        if (authService.existsByEmail(form.getEmail())) {
            model.addAttribute("erro", "Este e-mail já está cadastrado.");
            return "auth/register";
        }

        authService.register(form);
        redirectAttributes.addFlashAttribute("sucesso", "Conta criada! Faça login.");
        return "redirect:/auth/login";
    }
}