package com.brunch.service;

import com.brunch.dto.RegisterForm;
import com.brunch.model.Cart;
import com.brunch.model.RestaurantProfile;
import com.brunch.model.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;

    public UserService(FileStorageService fileStorageService, @Lazy PasswordEncoder passwordEncoder) {
        this.fileStorageService = fileStorageService;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterForm form) {
        String email = form.getEmail() != null ? form.getEmail().trim().toLowerCase() : "";
        if (fileStorageService.findUserByEmail(email).isPresent()) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        String role = (form.getRole() != null && form.getRole().equalsIgnoreCase("RESTAURANT"))
                ? "RESTAURANT" : "CUSTOMER";

        User user = new User();
        user.setFullName(form.getFullName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setPhone(form.getPhone().trim());
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = fileStorageService.saveUser(user);

        if ("CUSTOMER".equals(role)) {
            Cart cart = new Cart();
            cart.setCustomerId(savedUser.getId());
            fileStorageService.saveCart(cart);
        } else if ("RESTAURANT".equals(role)) {
            RestaurantProfile profile = new RestaurantProfile();
            profile.setUserId(savedUser.getId());
            profile.setRestaurantName(form.getRestaurantName() != null && !form.getRestaurantName().isBlank()
                    ? form.getRestaurantName().trim() : savedUser.getFullName() + "'s Kitchen");
            profile.setDescription("Delicious home-style food cooked fresh every day.");
            profile.setPhone(savedUser.getPhone());
            profile.setCity(form.getCity() != null && !form.getCity().isBlank() ? form.getCity().trim() : "Bangalore");
            profile.setAddress(form.getAddress() != null && !form.getAddress().isBlank() ? form.getAddress().trim() : "Main Street");
            profile.setOpeningTime("08:00 AM");
            profile.setClosingTime("11:00 PM");
            fileStorageService.saveRestaurant(profile);
        }

        return savedUser;
    }

    public Optional<User> findByEmail(String email) {
        return fileStorageService.findUserByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return fileStorageService.findUserById(id);
    }

    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return fileStorageService.findUserByEmail(auth.getName()).orElse(null);
    }
}
