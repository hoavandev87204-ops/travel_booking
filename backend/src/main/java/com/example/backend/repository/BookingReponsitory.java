package com.example.backend.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.backend.entity.Booking;
import com.example.backend.entity.Tour;
import com.example.backend.entity.User;

@Repository
public interface BookingReponsitory extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(UUID id);
    @Query("select b from Booking b join fetch b.tour where b.user.id = :userId order by b.bookings desc" )
    List<Booking> findByUserIdWithTour(@Param("userId") UUID userId);

    List<Booking> findByStatus(String status);
    boolean existsByUserAndTourAndStatus(User userId, Tour tourId, String status);

    @Query("""
        SELECT COALESCE(SUM(b.seat * t.price), 0)
        FROM Booking b
        JOIN b.tour t
        """)
        BigDecimal calculateTotalRevenue();
    // join detail
    @Query("select b from Booking b join fetch b.user join fetch b.tour")
    List<Booking> findbyWithDetail();
    // ticket detail
    @Query("SELECT b FROM Booking b " +
           "JOIN FETCH b.tour " +
           "JOIN FETCH b.user " +
           "WHERE b.id = :id")
    Optional<Booking> findByIdWithDetails(@Param("id") Long id);
}
    

