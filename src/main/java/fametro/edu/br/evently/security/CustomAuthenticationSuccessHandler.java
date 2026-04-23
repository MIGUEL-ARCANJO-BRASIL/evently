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

        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("");

        System.out.println(">>> ROLE DO USUÁRIO: " + role); // ← adiciona isso

        switch (role) {
            case "ROLE_ADMIN"        -> response.sendRedirect("/events");
            case "ROLE_ORGANIZADOR"  -> response.sendRedirect("/organizador/dashboard");
            case "ROLE_MEMBRO"       -> response.sendRedirect("/dashboard");
            default                  -> response.sendRedirect("/");
        }
    }}