package com.example.backend.dto;

import java.util.UUID;

public class ReviewRequest {
    private UUID userId;
    private Long tourId;
    private int rating;
    private String comment;

    // get==set
    public UUID getUserId() {
        return userId;
    }

    public Long getTourId() {
        return tourId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public void setUser(UUID userid) {
        this.userId = userid;
    }

    public void setTour(Long tourid) {
        this.tourId = tourid;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
