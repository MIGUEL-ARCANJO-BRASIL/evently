package fametro.edu.br.evently.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        Object principal = authentication.getPrincipal();
        if (principal instanceof fametro.edu.br.evently.user.model.User) {
            fametro.edu.br.evently.user.model.User user = (fametro.edu.br.evently.user.model.User) principal;
            if (user.getCpf() == null || user.getPhone() == null) {
                response.sendRedirect("/auth/complete-profile");
                return;
            }
        }

        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");

        System.out.println(">>> ROLE DO USUÁRIO: " + role); // ← adiciona isso

        switch (role) {
            case "ROLE_ADMIN"        -> response.sendRedirect("/events");
            case "ROLE_ORGANIZADOR"  -> response.sendRedirect("/organizador/dashboard");
            case "ROLE_MEMBRO"       -> response.sendRedirect("/events");
            default                  -> response.sendRedirect("/");
        }
    }
}