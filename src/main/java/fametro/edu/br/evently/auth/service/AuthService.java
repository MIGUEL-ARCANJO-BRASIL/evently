package fametro.edu.br.evently.auth.service;

import fametro.edu.br.evently.auth.dto.request.UserLoginRequestDTO;
import fametro.edu.br.evently.auth.dto.request.UserRegisterRequestDTO;
import fametro.edu.br.evently.user.model.User;
import fametro.edu.br.evently.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void login(UserLoginRequestDTO dto) {
        // A autenticação é gerenciada pelo Spring Security, então este método pode ser deixado vazio
        log.info("Tentativa de login - email: {}", dto.getEmail());
    }

    public void register(UserRegisterRequestDTO dto) {
        User user = User.builder().
                name(dto.getName()).email(dto.getEmail()).password(passwordEncoder.encode(dto.getPassword())).cpf(dto.getCpf()).role(dto.getRole()).build();
        log.info("Usuário {} cadastrado com sucesso", user.getEmail());
        this.userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        log.info("Usuário existe - email: {}", email);
        return this.userRepository.findByEmail(email).isPresent();
    }
}
