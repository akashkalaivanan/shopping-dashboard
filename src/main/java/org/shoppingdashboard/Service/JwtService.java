package org.shoppingdashboard.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    private final String SECRET_KEY = "Zdr8fOWt15X9zxipuKdE+ixTnSVyPW9zFcDsgk/EInKsN9n6VgX2bMpvlUyFi7Ht"; // Replace with a secure key

    // Generate a JWT token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours validity
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Validate the JWT token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Extract username from the JWT token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Extract all claims from the JWT token
    private Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
}