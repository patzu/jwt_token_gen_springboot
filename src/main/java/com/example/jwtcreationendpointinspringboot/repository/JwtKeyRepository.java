package com.example.jwtcreationendpointinspringboot.repository;

import com.example.jwtcreationendpointinspringboot.entity.JwtKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwtKeyRepository extends JpaRepository<JwtKey, Long> {
    Optional<JwtKey> findTopByOrderByCreatedAtDesc();
}
