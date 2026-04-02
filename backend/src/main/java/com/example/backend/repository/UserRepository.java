package com.example.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
    
    List<User> findByRole(String role);

    List<User> findByEnabled(boolean enabled);
}