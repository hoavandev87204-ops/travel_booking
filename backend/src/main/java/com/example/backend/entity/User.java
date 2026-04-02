package com.example.backend.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false, unique = true)
    private String email;
    @JsonProperty(access =JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;
    private String role;
    private boolean enabled = true;
    // time
    private LocalDateTime createAt;

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createAt = LocalDateTime.now();
    }
    public boolean isEnabled() {
    return enabled;
    }

public void setEnabled(boolean enabled) {
    this.enabled = enabled;
}

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
