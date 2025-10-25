package com.example.ecommerce.website.Payload;



import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

