package fametro.edu.br.evently.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/auth/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/uploads/**",
                                "/error"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/events", "/events/*").permitAll()
                        .requestMatchers("/events/new").hasRole("ADMIN")
                        .requestMatchers("/events/*/edit", "/events/*/archive").hasAnyRole("ADMIN", "ORGANIZADOR")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/organizador/**").hasAnyRole("ADMIN", "ORGANIZADOR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .successHandler(successHandler)
                        .failureUrl("/auth/login?erro=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        http.authenticationProvider(authenticationProvider());

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // Passamos o userDetailsService diretamente no construtor para satisfazer o erro
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);

        // O PasswordEncoder continua sendo via setter
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }
}