package com.example.backend.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // Thêm cái này
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;

@Service
public class UserService implements UserDetailsService { // Implement UserDetailsService để Spring Security hiểu

    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 1. Sửa hàm Đăng ký: Thêm mặc định enabled = true
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        
        user.setEnabled(true); // Luôn để true khi mới đăng ký
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // 2. Sửa hàm Login thủ công: Kiểm tra enabled
    public User loginUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // KIỂM TRA KHÓA: Nếu tài khoản bị khóa thì không cho login
            if (!user.isEnabled()) {
                throw new RuntimeException("Tài khoản của bạn đã bị khóa bởi Admin!");
            }

            if (passwordEncoder.matches(password, user.getPassword())) {
                user.setPassword(null); // Ẩn mật khẩu trước khi trả về client
                return user;
            }
        }
        throw new RuntimeException("Email hoặc mật khẩu không chính xác");
    }

    // 3. Sửa hàm loadUserByUsername cho Spring Security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        }

        // Truyền giá trị user.isEnabled() vào vị trí thứ 3 của UserDetails
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),      
                true,                 
                true,                  
                true,                  
              Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}