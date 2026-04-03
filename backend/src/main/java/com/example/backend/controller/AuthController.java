package com.example.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.User;
import com.example.backend.service.JwtService;
import com.example.backend.service.UserService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class AuthController {
    @Autowired
    private UserService userservice;
    @Autowired
    private JwtService jwtService;      

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User regisUser = userservice.registerUser(user);
            return ResponseEntity.ok(regisUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
    String email = credentials.get("email");
    String password = credentials.get("password");
    User user = userservice.loginUser(email, password);
    if (user != null) {
        String token = jwtService.generateToken(user.getUsername());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", user
        ));
    } else {
        return ResponseEntity.status(401).body("Sai mật khẩu hoặc email");
    }
}

}
