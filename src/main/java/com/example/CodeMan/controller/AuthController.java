package com.example.CodeMan.controller;

import com.example.CodeMan.dto.RegisterForm;
import com.example.CodeMan.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        if (!model.containsAttribute("registerForm"))
            model.addAttribute("registerForm", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerPost(@Valid @ModelAttribute RegisterForm registerForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors())
            return "auth/register";
        try {
            authService.registrar(registerForm);
            redirectAttributes.addFlashAttribute("success",
                    "Cuenta creada correctamente. Ahora puedes iniciar sesión.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("register.error", ex.getMessage());
            return "auth/register";
        }
    }
}
