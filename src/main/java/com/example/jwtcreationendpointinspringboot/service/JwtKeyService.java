package com.example.jwtcreationendpointinspringboot.service;

import com.example.jwtcreationendpointinspringboot.entity.JwtKey;
import com.example.jwtcreationendpointinspringboot.repository.JwtKeyRepository;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class JwtKeyService {
    JwtKeyRepository jwtKeyRepository;

    public JwtKeyService(JwtKeyRepository jwtKeyRepository) {
        this.jwtKeyRepository = jwtKeyRepository;
    }

    public String generateAndSaveKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();
        String encodeKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        JwtKey jwtKey = JwtKey.builder().secretKey(encodeKey).createdAt(LocalDateTime.now()).build();
        jwtKeyRepository.save(jwtKey);

        return encodeKey;
    }

    public String getLatestKey() throws NoSuchAlgorithmException {
        Optional<JwtKey> optionalJwtKey = jwtKeyRepository.findTopByOrderByCreatedAtDesc();
        if (optionalJwtKey.isPresent()) {
            JwtKey jwtKey = optionalJwtKey.get();
            return jwtKey.secretKey;
        } else {
            return generateAndSaveKey();
        }
    }
}
