package com.brunch.service;

import com.brunch.dto.RegisterForm;
import com.brunch.entity.Cart;
import com.brunch.entity.User;
import com.brunch.repository.CartRepository;
import com.brunch.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, CartRepository cartRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerCustomer(RegisterForm form) {
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }
        User user = new User();
        user.setFullName(form.getFullName().trim());
        user.setEmail(form.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setPhone(form.getPhone().trim());
        user.setRole("CUSTOMER");

        User savedUser = userRepository.save(user);

        Cart cart = new Cart(savedUser);
        cartRepository.save(cart);

        return savedUser;
    }

    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }
}
