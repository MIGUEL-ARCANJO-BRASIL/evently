package fametro.edu.br.evently.auth.service;

import fametro.edu.br.evently.auth.dto.UserRegisterDTO;
import fametro.edu.br.evently.core.util.MaskUtils;
import fametro.edu.br.evently.user.model.PasswordResetToken;
import fametro.edu.br.evently.user.model.User;
import fametro.edu.br.evently.user.repository.PasswordResetTokenRepository;
import fametro.edu.br.evently.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository tokenRepository;

    public void register(UserRegisterDTO dto) {
        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .cpf(MaskUtils.unmask(dto.getCpf()))
                .role(dto.getRole())
                .phone(MaskUtils.unmask(dto.getPhone()))
                .birthDate(dto.getBirthDate())
                .build();
        log.info("Usuário {} cadastrado com sucesso", user.getEmail());
        this.userRepository.save(user);
    }

    @Transactional
    public String verifyIdentityAndCreateToken(String email, String cpf, LocalDate birthDate) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("E-mail não encontrado."));

        String cleanCpf = MaskUtils.unmask(cpf);
        if (!user.getCpf().equals(cleanCpf)) {
            throw new RuntimeException("CPF não confere com o e-mail informado.");
        }

        if (user.getBirthDate() == null || !user.getBirthDate().equals(birthDate)) {
            throw new RuntimeException("Data de nascimento não confere.");
        }

        // Busca se já existe um token para este usuário e o reutiliza/atualiza
        PasswordResetToken resetToken = tokenRepository.findByUser(user)
                .orElse(new PasswordResetToken());

        resetToken.setUser(user);
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));

        tokenRepository.save(resetToken);
        return resetToken.getToken();
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token de redefinição inválido ou já utilizado."));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("O link de redefinição expirou. Por favor, solicite um novo.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Remove o token após o uso
        tokenRepository.delete(resetToken);
        log.info("Senha do usuário {} redefinida com sucesso.", user.getEmail());
    }

    public boolean existsByEmail(String email) {
        log.info("Usuário existe - email: {}", email);
        return this.userRepository.findByEmail(email).isPresent();
    }

    public boolean existsByCpf(String cpf) {
        log.info("Usuário existe - userCpf: {}", cpf);
        return this.userRepository.findByCpf(cpf).isPresent();
    }

}
