package com.example.backend.controller.user;

import java.util.List;
import  java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.BookingRequest;
import com.example.backend.entity.Booking;
import com.example.backend.repository.BookingReponsitory;
import com.example.backend.service.BookingService;

@RestController
@RequestMapping("/api/user/booking")
@CrossOrigin(origins = "http://localhost:3000")
public class UserBookingController {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingReponsitory bookingReponsitory;

    @GetMapping
    public List<Booking> getBooking() {
        return bookingReponsitory.findbyWithDetail();
    }
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Booking>> getHistory(@PathVariable UUID userId){
        List<Booking> history = bookingService.getHistoryBooking(userId);
        return ResponseEntity.ok(history);
    }

    @PostMapping
    public ResponseEntity<Booking> Bookingtour(
            @RequestBody BookingRequest booking) {
        return ResponseEntity.ok(bookingService.createBooking(
                booking.getUserId(),
                booking.getTourId(),
                booking.getSeat()));
    }
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long id){
        bookingService.DeleteBooking(id);
        return ResponseEntity.ok("Xóa tour thành công");
    }
    // show tichket
    @GetMapping("/ticket/{id}")
    public ResponseEntity<Booking> showTicketDetail(@PathVariable Long id) {
    // Gọi service để lấy chi tiết booking kèm theo Tour và User
    Booking booking = bookingService.getBookingDetail(id);
    
    if (booking != null) {
        return ResponseEntity.ok(booking);
    } else {
        return ResponseEntity.notFound().build();
    }
}
}
