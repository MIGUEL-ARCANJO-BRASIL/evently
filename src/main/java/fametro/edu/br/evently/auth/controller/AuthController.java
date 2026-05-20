package fametro.edu.br.evently.auth.controller;

import fametro.edu.br.evently.auth.dto.UserRegisterDTO;
import fametro.edu.br.evently.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final fametro.edu.br.evently.user.service.UserService userService;
    private final AuthenticationManager authenticationManager;

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
            HttpServletRequest request,
            HttpServletResponse response) {

        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            authService.register(form);

            UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(
                    form.getEmail(),
                    form.getPassword());

            Authentication authentication = authenticationManager.authenticate(authRequest);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
            log.info("Usuário cadastrado com sucesso e autenticado: {}", form.getEmail());
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email,
            @RequestParam String cpf,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd") java.time.LocalDate birthDate,
            Model model) {
        try {
            String token = authService.verifyIdentityAndCreateToken(email, cpf, birthDate);
            // Redireciona diretamente para a tela de redefinição, já que validamos a
            // identidade aqui
            return "redirect:/auth/reset-password?token=" + token;
        } catch (Exception e) {
            model.addAttribute("mensagemErro", e.getMessage());
            model.addAttribute("email", email);
            model.addAttribute("cpf", cpf);
            return "auth/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token, @RequestParam String password, Model model) {
        try {
            authService.resetPassword(token, password);
            model.addAttribute("mensagemSucesso", "Senha redefinida com sucesso! Agora você pode entrar.");
            return "auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("mensagemErro", e.getMessage());
            model.addAttribute("token", token);
            return "auth/reset-password";
        }
    }

    @GetMapping("/complete-profile")
    public String completeProfileForm(Model model, @org.springframework.security.core.annotation.AuthenticationPrincipal fametro.edu.br.evently.user.model.User user) {
        if (user == null) {
            return "redirect:/auth/login";
        }
        if (user.getCpf() != null && user.getPhone() != null) {
            return "redirect:/"; // Já completou
        }
        model.addAttribute("profileForm", new fametro.edu.br.evently.auth.dto.CompleteProfileDTO());
        return "auth/complete-profile";
    }

    @PostMapping("/complete-profile")
    public String completeProfile(
            @Valid @ModelAttribute("profileForm") fametro.edu.br.evently.auth.dto.CompleteProfileDTO form,
            BindingResult result,
            @org.springframework.security.core.annotation.AuthenticationPrincipal fametro.edu.br.evently.user.model.User user,
            Model model) {
        
        if (user == null) {
            return "redirect:/auth/login";
        }
        
        if (result.hasErrors()) {
            return "auth/complete-profile";
        }

        try {
            userService.completeProfile(user.getId(), form);
            
            // Redireciona com base na role, usando a mesma lógica do SuccessHandler
            String role = user.getAuthorities().stream().findFirst().map(org.springframework.security.core.GrantedAuthority::getAuthority).orElse("");
            return switch (role) {
                case "ROLE_ADMIN" -> "redirect:/events";
                case "ROLE_ORGANIZADOR" -> "redirect:/organizador/dashboard";
                case "ROLE_MEMBRO" -> "redirect:/events";
                default -> "redirect:/";
            };
        } catch (Exception e) {
            model.addAttribute("mensagemErro", e.getMessage());
            return "auth/complete-profile";
        }
    }
}

//