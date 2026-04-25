package fametro.edu.br.evently.user.controller;

import fametro.edu.br.evently.user.dto.UserEditInfosForm;
import fametro.edu.br.evently.user.model.User;
import fametro.edu.br.evently.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());

        if (!model.containsAttribute("userForm")) {
            model.addAttribute("userForm", new UserEditInfosForm(
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getCpf(),
                    user.getBirthDate()
            ));
        }

        model.addAttribute("user", user);
        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("userForm") UserEditInfosForm form,
                                BindingResult result,
                                @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request,
                                HttpServletResponse response) {

        User user = userService.findByEmail(userDetails.getUsername());

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("editMode", true);
            return "user/profile";
        }

        try {
            userService.updateUserInfos(user.getId(), form, avatarFile);

            User updatedUser = userService.findByEmail(user.getEmail());

            UsernamePasswordAuthenticationToken newAuth =
                    new UsernamePasswordAuthenticationToken(
                            updatedUser,
                            userDetails.getPassword(),
                            updatedUser.getAuthorities()
                    );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(newAuth);
            SecurityContextHolder.setContext(context);

            request.getSession(true).setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    context
            );

            redirectAttributes.addFlashAttribute("sucesso", "Perfil atualizado com sucesso.");
        } catch (RuntimeException e) {
            model.addAttribute("user", user);
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("editMode", true);
            return "user/profile";
        }

        return "redirect:/user/profile";
    }
}