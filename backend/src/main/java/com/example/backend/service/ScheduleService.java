package com.example.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.entity.Schedule;
import com.example.backend.repository.ScheduleRepository;
import com.example.backend.repository.TourRepository;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    
    @Autowired
    private TourRepository tourRepository; // Giả định bạn đã có TourRepository

    public List<Schedule> getItinerariesByTour(Long tourId) {
        return scheduleRepository.findByTourIdOrderByDayNumberAsc(tourId);
    }

    public Schedule addItinerary(Long tourId, Schedule itinerary) {
        return tourRepository.findById(tourId).map(tour -> {
            itinerary.setTour(tour);
            return scheduleRepository.save(itinerary);
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy Tour với ID: " + tourId));
    }

    public void deleteItinerary(Long id) {
        if (!scheduleRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy lịch trình với ID: " + id);
        }
        scheduleRepository.deleteById(id);
    }

    // Chức năng Sửa
    public Schedule updateItinerary(Long id, Schedule details) {
        Schedule itinerary = scheduleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch trình với ID: " + id));
        
        // Cập nhật các trường thông tin
        itinerary.setDayNumber(details.getDayNumber());
        itinerary.setTitle(details.getTitle());
        itinerary.setLocation(details.getLocation());
        itinerary.setDescription(details.getDescription());
        
        return scheduleRepository.save(itinerary);
    }
}
