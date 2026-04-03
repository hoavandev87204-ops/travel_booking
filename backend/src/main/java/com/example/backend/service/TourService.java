package com.example.backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.entity.Category;
import com.example.backend.entity.Tour;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.TourRepository;

@Service
public class TourService {
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    // detail tour
    public List<Tour> getAllTour() {
        return tourRepository.findAll();
    }

    // findtour
    public List<Tour> findTour(String search, String category, String date) {

        LocalDate parseDate = null;

        if (date != null && !date.isEmpty()) {
            try {
                parseDate = LocalDate.parse(date);
            } catch (Exception e) {
                return Collections.emptyList();
            }
        }

        String title = (search != null && !search.trim().isEmpty()) ? search : null;
        String cateName = (category != null && !category.trim().isEmpty()) ? category : null;

        // FULL: title + category + date (range)
        if (title != null && cateName != null && parseDate != null) {
            return tourRepository
                    .findByTitleContainingIgnoreCaseAndCategory_NameContainingIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                            title, cateName, parseDate, parseDate);
        }

        // title + date
        if (title != null && parseDate != null) {
            return tourRepository
                    .findByTitleContainingIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                            title, parseDate, parseDate);
        }

        // category + date
        if (cateName != null && parseDate != null) {
            return tourRepository
                    .findByCategory_NameContainingIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                            cateName, parseDate, parseDate);
        }

        // chỉ date
        if (parseDate != null) {
            return tourRepository
                    .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
                            parseDate, parseDate);
        }

        // title
        if (title != null) {
            return tourRepository.findByTitleContainingIgnoreCase(title);
        }

        // category
        if (cateName != null) {
            return tourRepository.findByCategory_NameContainingIgnoreCase(cateName);
        }

        return tourRepository.findAll();
    }

    // find tourcategory
    // add new tour

    public Tour creaTour(Tour tour, Long CategoryId) {
        if (tour.getEndDate().isBefore(tour.getStartDate())) {
            throw new RuntimeException("Ngày kết thúc không thể trước ngày băt đầu");
        }
        if (tour.getMaxSlot() == null || tour.getMaxSlot() <= 0) {
            throw new RuntimeException("Số lượng người tham gia lớn hơn không");
        }
        if(tour.getImage() ==null || tour.getImage().isEmpty()){
            
            throw  new RuntimeException("Tour phải có ít nhất 1 hình ảnh minh họa");
        }
        // setcategory->tour
        Category category = categoryRepository.findById(CategoryId)
                .orElseThrow(() -> new RuntimeException("không tìm thấy danh mục" + CategoryId));
        tour.setCategory(category);
        return tourRepository.save(tour);
    }
    @Transactional
    public Tour updateTour(Long id, Tour tourDetails, Long categoryId) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tour với ID: " + id));
        tour.setTitle(tourDetails.getTitle());
        tour.setDescription(tourDetails.getDescription());
        tour.setPrice(tourDetails.getPrice());
        tour.setMaxSlot(tourDetails.getMaxSlot());
        tour.setTimeRegister(tourDetails.getTimeRegister());
        tour.setStartDate(tourDetails.getStartDate());
        tour.setEndDate(tourDetails.getEndDate());
        tour.setImage(tourDetails.getImage());
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + categoryId));
        tour.setCategory(category);
        return tourRepository.save(tour);
    }

    // update number current
    public Tour UpdateCurrentSlots(Long tourId, int seatbook) {
        if(seatbook <=0){
            throw new RuntimeException("Số lượng người tham gia phải lớn hơn không");
        }
        return tourRepository.findById(tourId).map(tour -> {
            int newslot = tour.getCurrentSlot() + seatbook;
            if (newslot > tour.getMaxSlot()) {
                throw new RuntimeException("Tour hết chỗ");
            }
            tour.setCurrentSlot(newslot);
            return tourRepository.save(tour);
        }).orElseThrow(() -> new RuntimeException("Không tìm thấy tour"));
    }
    // filter tour
   public List<Tour> filterTour(String search, String category, String date, String price) {

    LocalDate parseDate = null;
    BigDecimal parsePrice = null;

    if (date != null && !date.isEmpty()) {
        try {
            parseDate = LocalDate.parse(date);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    if (price != null && !price.isEmpty()) {
        try {
            parsePrice = new BigDecimal(price);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    String keyword = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
    final LocalDate finalDate = parseDate;
    final BigDecimal finalPrice = parsePrice;
    return tourRepository.findAll().stream()
        .filter(t -> keyword  == null || t.getTitle().toLowerCase().contains(keyword .toLowerCase()))
        .filter(t -> category == null || t.getCategory().getId().toString().equals(category))
        .filter(t -> finalPrice == null || t.getPrice().compareTo(finalPrice) <= 0)
        .filter(t -> finalDate == null || !t.getStartDate().isAfter(finalDate)).toList();
}

    // delete tour
    public void deleteTour(Long id) {
        tourRepository.deleteById(id);
    }
}
