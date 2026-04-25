package fametro.edu.br.evently.auth.service;

import fametro.edu.br.evently.auth.dto.UserLoginDTO;
import fametro.edu.br.evently.auth.dto.UserRegisterDTO;
import fametro.edu.br.evently.user.model.User;
import fametro.edu.br.evently.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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

    public void register(UserRegisterDTO dto) {
        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .cpf(dto.getCpf())
                .role(dto.getRole())
                .phone(dto.getPhone())
                .birthDate(dto.getBirthDate())
                .build();
        log.info("Usuário {} cadastrado com sucesso", user.getEmail());
        this.userRepository.save(user);
    }

    public boolean existsByEmail(String email) {
        log.info("Usuário existe - email: {}", email);
        return this.userRepository.findByEmail(email).isPresent();
    }

    public boolean existsByCpf(String cpf) {
        log.info("Usuário existe - cpf: {}", cpf);
        return this.userRepository.findByCpf(cpf).isPresent();
    }

}
