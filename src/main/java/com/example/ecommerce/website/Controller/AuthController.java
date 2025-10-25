package com.example.ecommerce.website.Controller;



import com.example.ecommerce.website.Jwt.Util.JwtUtil;
import com.example.ecommerce.website.Payload.LoginRequest;
import com.example.ecommerce.website.Payload.UserRegistrationRequest;
import com.example.ecommerce.website.Services.AuthService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService,
                          JwtUtil jwtUtil,
                          AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }
@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody UserRegistrationRequest registrationRequest) {
    try {
        authService.registerUser(
            registrationRequest.getUsername(),
            registrationRequest.getPassword(),
            "ROLE_USER"   // ✅ default role
        );
        return ResponseEntity.ok("User registered successfully");
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}


@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    try {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
    } catch (Exception e) {
        return ResponseEntity.badRequest().body("Incorrect username or password");
    }

    final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
    final String jwt = jwtUtil.generateToken(userDetails.getUsername());

    // ✅ return JSON instead of plain text
    return ResponseEntity.ok(Map.of("token", jwt));
}

}


