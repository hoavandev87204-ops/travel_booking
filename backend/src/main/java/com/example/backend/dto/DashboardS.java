package com.example.backend.dto;

import java.math.BigDecimal;

public class DashboardS {
    private BigDecimal totalRevenue;
    private Long totalBooking;
    private Long totalTour;

    public DashboardS(BigDecimal totalRevenue, Long totalBooking, Long totalTour) {
        this.totalRevenue = totalRevenue;
        this.totalBooking = totalBooking;
        this.totalTour = totalTour;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public Long getTotalBooking() {
        return totalBooking;
    }

    public Long getTotalTour() {
        return totalTour;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public void settotalBooking(Long totalBooking) {
        this.totalBooking = totalBooking;
    }

    public void settotalTour(Long totalTour) {
        this.totalTour = totalTour;
    }
}