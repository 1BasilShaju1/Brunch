package com.brunch.controller;

import com.brunch.dto.LoginForm;
import com.brunch.dto.RegisterForm;
import com.brunch.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String handleRegistration(@Valid @ModelAttribute("registerForm") RegisterForm registerForm,
                                     BindingResult bindingResult,
                                     Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            userService.registerUser(registerForm);
            return "redirect:/login?registered=true";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }
}
