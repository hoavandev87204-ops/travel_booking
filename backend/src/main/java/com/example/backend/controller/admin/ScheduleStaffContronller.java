package com.example.backend.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.Schedule;
import com.example.backend.service.ScheduleService;

@RestController
@RequestMapping("/api/admin/staff")
@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*")
public class ScheduleStaffContronller {

    @Autowired
    private ScheduleService itineraryService;

    // --- DÀNH CHO USER (XEM LỊCH TRÌNH) ---
    @GetMapping("/tours/{tourId}/itineraries")
    public ResponseEntity<List<Schedule>> getItineraries(@PathVariable Long tourId) {
        return ResponseEntity.ok(itineraryService.getItinerariesByTour(tourId));
    }
    // Thêm lịch trình mới cho Tour
    @PostMapping("/admin/tours/{tourId}/itineraries")
    @PreAuthorize("permitAll()") // Chỉ Admin mới được thêm
    public ResponseEntity<Schedule> createItinerary(
            @PathVariable Long tourId, 
            @RequestBody Schedule itinerary) {
        return ResponseEntity.ok(itineraryService.addItinerary(tourId, itinerary));
    }

    // Xóa một mục lịch trình
    @PreAuthorize("permitAll()")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItinerary(@PathVariable Long id) {
        try {
            itineraryService.deleteItinerary(id);
            return ResponseEntity.ok("Đã xóa lịch trình thành công ID: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // 2. Chức năng SỬA (Cập nhật)
    @PreAuthorize("permitAll()")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItinerary(@PathVariable Long id, @RequestBody Schedule itinerary) {
        try {
            Schedule updated = itineraryService.updateItinerary(id, itinerary);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}