package com.example.backend.controller.user;
import  java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.Tour;
import com.example.backend.repository.TourRepository;
import com.example.backend.service.TourService;
@RestController
@RequestMapping("/api/user/tours")
public class UserTourController {

    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private TourService tourService;
    @GetMapping("/{id}")
    public ResponseEntity<Tour> getTourById(@PathVariable Long id) {
        return tourRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/filterfindtour")
    public List<Tour> filter( @RequestParam (required=false)String keyword,
    @RequestParam (required=false)String categoryId,
    @RequestParam (required=false)String startDate,
    @RequestParam (required=false)String maxPrice){
        return tourService.filterTour(keyword, categoryId, startDate, maxPrice);
    }
}
