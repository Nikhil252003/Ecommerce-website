package com.example.ecommerce.website.Jwt.Util;




import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A utility class for generating and validating JSON Web Tokens (JWTs).
 * This class handles all the JWT-related cryptographic operations.
 */
@Component
public class JwtUtil {

    // Inject the secret key from application.properties
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private Key getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the username from the token.
     * @param token The JWT.
     * @return The username.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the token.
     * @param token The JWT.
     * @return The expiration date.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the token.
     * @param token The JWT.
     * @param claimsResolver A function to resolve the claim.
     * @return The claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    /**
     * Generates a JWT for a given username.
     * @param username The user's username.
     * @return The generated JWT.
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        // No extra claims needed for now, but you can add roles, etc. here.
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
    // ... (existing code)
    long expirationTime = 1000 * 60 * 60 * 24 * 3;
    
    // ✅ Add this line to see the fresh token being generated
    String newToken = Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
        
    System.out.println("Generated new token with Issued At: " + new Date(System.currentTimeMillis()));
    System.out.println("Token: " + newToken);

    return newToken;
}

    /**
     * Validates a JWT.
     * @param token The JWT.
     * @param username The user's username.
     * @return True if the token is valid, false otherwise.
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
