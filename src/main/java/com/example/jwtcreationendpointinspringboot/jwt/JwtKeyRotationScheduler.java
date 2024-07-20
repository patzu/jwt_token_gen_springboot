package com.example.jwtcreationendpointinspringboot.jwt;

import com.example.jwtcreationendpointinspringboot.service.JwtKeyService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Component
public class JwtKeyRotationScheduler {

    private final JwtKeyService jwtKeyService;

    public JwtKeyRotationScheduler(JwtKeyService jwtKeyService) {
        this.jwtKeyService = jwtKeyService;
    }

    @Scheduled(fixedDelay = 86400000)
    public void schedule() throws NoSuchAlgorithmException {

        String generatedKey = jwtKeyService.generateAndSaveKey();
        System.out.println("Rotated JWT key is done at: " + LocalDateTime.now() +
                " and the new created key is: " + generatedKey);
    }
}
