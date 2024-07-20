package com.example.jwtcreationendpointinspringboot.controller;

import com.example.jwtcreationendpointinspringboot.service.JwtKeyService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

@RestController
public class AuthController {

    private final JwtKeyService jwtKeyService;

    public AuthController(JwtKeyService jwtKeyService) {
        this.jwtKeyService = jwtKeyService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) throws NoSuchAlgorithmException {
        // Validate user credentials (simplified)
        if ("user".equals(loginRequest.getUsername()) && "password".equals(loginRequest.getPassword())) {

            String secretKeyBase64 = jwtKeyService.getLatestKey();
            byte[] decodedKey = Base64.getDecoder().decode(secretKeyBase64);
            SecretKey key = Keys.hmacShaKeyFor(decodedKey);


            String token = Jwts.builder()
                    .subject(loginRequest.getUsername())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day expiration
                    .signWith(key)
                    .compact();
            return ResponseEntity.ok(new JwtResponse(token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}

@Getter
class LoginRequest {
    private String username;
    private String password;
}

class JwtResponse {
    private String token;

    public JwtResponse(String token) {
        this.token = token;
    }
}
