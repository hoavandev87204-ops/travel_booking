package com.example.backend.controller.admin;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.DashboardS;
import com.example.backend.entity.Booking;
import com.example.backend.entity.User;
import com.example.backend.repository.BookingReponsitory;
import com.example.backend.repository.TourRepository;
import com.example.backend.repository.UserRepository;

@RestController
@RequestMapping("api/admin/stats")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminStaticController {
    @Autowired
    private BookingReponsitory bookingReponsitory;
    @Autowired
    TourRepository tourRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("sumary")
    public DashboardS getDashboardS() {
        BigDecimal totalRevenue = bookingReponsitory.calculateTotalRevenue();
        if (totalRevenue == null)
            totalRevenue = BigDecimal.ZERO;
        Long totalBooking = bookingReponsitory.count();
        Long totalTour = tourRepository.count();
        return new DashboardS(totalRevenue, totalBooking, totalTour);
    }

    @GetMapping("booking")
    public List<Booking> getBooking() {
        return bookingReponsitory.findbyWithDetail();
    }
    // Lấy toàn bộ danh sách User
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !"ADMIN".equals(user.getRole()))
                .collect(Collectors.toList());
    }

    @PatchMapping("/users/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserStatus(@PathVariable UUID id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        if ("ADMIN".equals(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Không được phép thao tác trên tài khoản Quản trị viên!");
        }

        boolean newStatus = !user.isEnabled();
        user.setEnabled(newStatus);
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", user.isEnabled() 
            ? "Đã mở khóa tài khoản thành công" 
            : "Đã khóa tài khoản thành công");
        response.put("isEnabled", user.isEnabled());
        response.put("username", user.getUsername());

        return ResponseEntity.ok(response);
    }

}