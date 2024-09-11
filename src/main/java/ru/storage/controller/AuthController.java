package ru.storage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.storage.dto.UserRegistrationDTO;
import ru.storage.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login_page";
    }

    @GetMapping("/registration")
    public String showRegistrationPage(@ModelAttribute UserRegistrationDTO userRegistrationDTO) {
        return "auth/registration_page";
    }

    @PostMapping("/process_registration")
    public String registrateNewUser(@ModelAttribute @Valid UserRegistrationDTO userRegistrationDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "auth/registration_page";
        }

        userService.save(userRegistrationDTO);
        return "redirect:/auth/login?success";
    }
}
