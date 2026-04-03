package com.example.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.entity.Review;
import com.example.backend.entity.Tour;
import com.example.backend.entity.User;
import com.example.backend.repository.BookingReponsitory;
import com.example.backend.repository.ReviewReponsitory;
import com.example.backend.repository.TourRepository;
import com.example.backend.repository.UserRepository;

@Service
public class ReviewService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private ReviewReponsitory reviewReponsitory;
    @Autowired
    private BookingReponsitory bookingReponsitory;

    @Transactional
    public Review creatReview(UUID userid, Long tourid, int rating, String comment) {
        if (userid == null || tourid == null) {
            throw new RuntimeException("id người dùng và tour không được để trống");
        }
        User user = userRepository.findById(userid).orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        Tour tour = tourRepository.findById(tourid).orElseThrow(() -> new RuntimeException("Không tìm thấy tour"));
        // check user buy tour ?

        boolean checkBooking = bookingReponsitory.existsByUserAndTourAndStatus(user, tour, "COMFIRMED");
        if (!checkBooking) {
            throw new RuntimeException("user chưa đặt tour");
        }
        Review review = new Review();
        review.setUser(user);
        review.setTour(tour);
        review.setRating(rating);
        review.setComment(comment);
        return reviewReponsitory.save(review);
    }

    // show
    public List<Review> getReviewbyTour(Long tourid) {
        return reviewReponsitory.findByTourId(tourid);
    }

}
