package com.example.ecommerce.website;

import com.example.ecommerce.website.Jwt.JwtRequestFilter;
import com.example.ecommerce.website.Services.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
@Autowired
    private final JwtRequestFilter jwtRequestFilter;
    @Autowired
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, UserDetailsServiceImpl userDetailsService) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
 @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF protection, which is not needed for stateless APIs
            .csrf(AbstractHttpConfigurer::disable)
            // Configure CORS to allow requests from the React frontend
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Define authorization rules for all HTTP requests
            .authorizeHttpRequests(authorize -> authorize
                // Allow public access to all /auth and /products endpoints
                .requestMatchers(HttpMethod.POST, "/cart/**").hasAnyRole("ADMIN", "USER")
            .requestMatchers(HttpMethod.POST, "/categories/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")
            .requestMatchers(HttpMethod.POST, "/orders/**").authenticated() // ✅ Add this line

                // Authenticated users can access GET requests to /orders (already there)
                .requestMatchers(HttpMethod.GET, "/orders/**").authenticated()

            .requestMatchers("/cart/**").authenticated()

    
                .requestMatchers("/products/**").permitAll()
                .requestMatchers("/orders/**").authenticated()
            
            // 2. ✅ Then, less specific public access rules:
            .requestMatchers("/auth/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/categories/**", "/products/**").permitAll()
            
            // 3. ❌ The most general rule should be last:
            .anyRequest().authenticated()
                // Require authentication for all other requests
                
            )
            // Configure session management to be stateless, as we are using JWTs
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Add the JWT filter before the standard authentication filter
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

   
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000")); // ✅ explicit
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Collections.singletonList("*"));
    configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

   
}
