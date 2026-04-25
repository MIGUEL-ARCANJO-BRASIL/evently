package fametro.edu.br.evently.auth.controller;

import fametro.edu.br.evently.auth.dto.UserRegisterDTO;
import fametro.edu.br.evently.auth.service.AuthService;
import fametro.edu.br.evently.user.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "erro", required = false) String erro,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (erro != null) {
            model.addAttribute("mensagemErro", "E-mail ou senha incorretos.");
        }
        if (logout != null) {
            model.addAttribute("mensagemSucesso", "Você saiu com sucesso!");
        }
        return "auth/login"; // caminho do seu html de login
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("usuarioForm", new UserRegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("usuarioForm") UserRegisterDTO form,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "auth/register";
        }

        if (authService.existsByEmail(form.getEmail())) {
            model.addAttribute("erro", "Este e-mail já está cadastrado.");
            return "auth/events";
        }

        if (authService.existsByCpf(form.getCpf())) {
            model.addAttribute("erro", "Este CPF já está cadastrado.");
            return "auth/register";
        }

        authService.register(form);
        redirectAttributes.addFlashAttribute("sucesso", "Conta criada! Faça login.");
        return "redirect:/home";
    }

}