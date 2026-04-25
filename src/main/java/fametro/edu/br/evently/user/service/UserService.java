package fametro.edu.br.evently.user.service;

import fametro.edu.br.evently.user.dto.UserEditInfosForm;
import fametro.edu.br.evently.user.model.User;
import fametro.edu.br.evently.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private static final String UPLOAD_DIR = "uploads";
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("png", "jpg", "jpeg", "webp");

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public void updateUserInfos(UUID userId, UserEditInfosForm form, MultipartFile avatarFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.setName(form.name());
        user.setEmail(form.email());
        user.setPhone(form.phone());
        user.setCpf(form.cpf());
        user.setBirthDate(form.birthDate());

        if (avatarFile != null && !avatarFile.isEmpty()) {
            String fileName = saveAvatar(avatarFile);
            user.setAvatar(fileName);
        }

        userRepository.save(user);
    }

    private String saveAvatar(MultipartFile file) {
        try {
            String originalName = file.getOriginalFilename();
            String extension = getExtension(originalName);

            if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
                throw new RuntimeException("Formato de imagem inválido.");
            }

            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID() + "." + extension;
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar avatar.");
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new RuntimeException("Arquivo inválido.");
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}