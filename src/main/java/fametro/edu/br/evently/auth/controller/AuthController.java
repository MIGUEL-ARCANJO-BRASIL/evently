package fametro.edu.br.evently.auth.controller;

import fametro.edu.br.evently.auth.dto.UserRegisterDTO;
import fametro.edu.br.evently.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class AuthController {

    private final AuthService authService;
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

            UsernamePasswordAuthenticationToken authRequest =
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            form.getEmail(),
                            form.getPassword()
                    );

            Authentication authentication = authenticationManager.authenticate(authRequest);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            HttpSessionSecurityContextRepository securityContextRepository =
                    new HttpSessionSecurityContextRepository();
            securityContextRepository.saveContext(context, request, response);

            return "redirect:/events";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            return "auth/register";
        }
    }
}