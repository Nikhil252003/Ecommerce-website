package com.example.ecommerce.website.Services;



import com.example.ecommerce.website.Entities.User;
import com.example.ecommerce.website.Repositories.UserRepository;
import com.example.ecommerce.website .Entities.Cart;

import com.example.ecommerce.website.Repositories.CartRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, CartRepository cartRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
    }

 @Transactional
public void registerUser(String username, String password, String role) {
    if (userRepository.findByUsername(username).isPresent()) {
        throw new RuntimeException("User with this name already exists");
    }

    User newUser = new User();
    newUser.setUsername(username);
    newUser.setPassword(passwordEncoder.encode(password));

    // ✅ Normalize role to avoid ROLE_ROLE_ issue
    if (role == null || role.isBlank()) {
        newUser.setRole("ROLE_USER");
    } else {
        String normalizedRole = role.toUpperCase();
        if (!normalizedRole.startsWith("ROLE_")) {
            normalizedRole = "ROLE_" + normalizedRole;
        }
        newUser.setRole(normalizedRole);
    }

    Cart newCart = new Cart();
    cartRepository.save(newCart);

    newUser.setCart(newCart);
    userRepository.save(newUser);
}
}