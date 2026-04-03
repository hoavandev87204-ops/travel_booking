package com.example.backend.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.ReviewRequest;
import com.example.backend.entity.Review;
import com.example.backend.service.ReviewService;

@RestController
@RequestMapping("/api/user/review")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> ReviewTour(@RequestBody ReviewRequest reviewRequest) {
        try {
            Review saveReview = reviewService.creatReview(
                    reviewRequest.getUserId(),
                    reviewRequest.getTourId(),
                    reviewRequest.getRating(),
                    reviewRequest.getComment());
            return ResponseEntity.ok(saveReview);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/tour/{tourid}")
    public ResponseEntity<List<Review>> getallReview(@PathVariable Long tourid) {
        List<Review> reviews = reviewService.getReviewbyTour(tourid);
        return ResponseEntity.ok(reviews);
    }
}
