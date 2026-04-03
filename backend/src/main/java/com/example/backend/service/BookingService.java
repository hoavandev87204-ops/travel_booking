package com.example.backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.entity.Booking;
import com.example.backend.entity.Tour;
import com.example.backend.entity.User;
import com.example.backend.repository.BookingReponsitory;
import com.example.backend.repository.TourRepository;
import com.example.backend.repository.UserRepository;

@Service
public class BookingService {
    @Autowired
    private BookingReponsitory bookingReponsitory;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private UserRepository userRepository;
    public List<Booking> getAllBooking() {
        return bookingReponsitory.findAll();
    }
    // show booking 
    public List<Booking> getHistoryBooking(UUID userId){
        return bookingReponsitory.findByUserIdWithTour(userId);
    }
    //show ticket
    public Booking getBookingDetail(Long id) {
    return bookingReponsitory.findByIdWithDetails(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy vé với ID: " + id));
    }

    @Transactional // tạo true->rollback
    // detail
    public Booking createBooking(UUID userId, Long tourId, int seatToBook) {
        if (seatToBook < 0) {
            throw new RuntimeException("Số người đặt không được <0");
        }
        // find tour and user
        Tour tour = tourRepository.findById(tourId).orElseThrow(() -> new RuntimeException("Không tìm thấy tour"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        // check seatbook
        if (tour.getCurrentSlot() + seatToBook > tour.getMaxSlot()) {
            throw new RuntimeException("Tour không đủ chỗ" + seatToBook + "Người");
        }
        if(tour.getTimeRegister() != null && tour.getTimeRegister().isBefore(java.time.LocalDate.now())){
            throw new RuntimeException("Hạn đăng ký đã hết, không thể đặt tour");
        }
        // update seatbook
        // tour.setCurrentSlot(tour.getCurrentSlot() + seatToBook);
        // tourRepository.save(tour);
        // create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTour(tour);
        booking.setSeat(seatToBook);
        booking.setStatus("Pending");
        // sum=tour*seatbook
        BigDecimal price = tour.getPrice().multiply(BigDecimal.valueOf(seatToBook));
        booking.setTotalPrice(price);
        return bookingReponsitory.save(booking);

    }
    // update
    @Transactional
    public void confirmBooking(Long id){
        Booking booking = bookingReponsitory.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt tour với ID: " + id));
        Tour tour = booking.getTour();
        int updateSlot =  tour.getCurrentSlot()+booking.getSeat();
        if(updateSlot > tour.getMaxSlot()){
            throw new RuntimeException("Tour đã hết chỗ,không thể xác nhận thêm");
        }
        tour.setCurrentSlot(updateSlot);
        tourRepository.save(tour);
        booking.setStatus("Confirmed"); 
        bookingReponsitory.save(booking);

    }
    @Transactional
    public void AdminDeleteTourConfirm(Long id){
        Booking booking = bookingReponsitory.findById(id).orElseThrow(()-> new RuntimeException("Không tìm thấy id tour:"+id));
        if("Confirmed".equalsIgnoreCase(booking.getStatus())){
            Tour tour = booking.getTour();
            int seatbook = booking.getSeat();
            tour.setCurrentSlot(tour.getCurrentSlot() - seatbook);
            tourRepository.save(tour);
        }
        booking.setStatus("Canceled");
        bookingReponsitory.save(booking);
        
    }
    public void DeleteBooking(Long id){
        Booking booking = bookingReponsitory.findById(id).orElseThrow(()-> new RuntimeException("Không tìm thấy đơn đặt tour"));
        if("Confirmed".equals(booking.getStatus())){
            throw new RuntimeException("Tour đã được xác nhận không thể xóa");
        }
        bookingReponsitory.deleteById(id);
    }
}
