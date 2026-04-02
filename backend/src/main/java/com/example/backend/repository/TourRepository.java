package com.example.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.entity.Tour;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long> {
    // tour -> category
    List<Tour> findByCategoryId(Long category);

    // find nameTour( từ khóa)
    List<Tour> findByTitleContainingIgnoreCase(String keyword);

    // find tourcategory
    List<Tour> findByCategory_NameContainingIgnoreCase(String category);

    // find tour cheap price
    List<Tour> findByPriceLessThanEqual(Double price);

    // find >slot ->sql
    @org.springframework.data.jpa.repository.Query("SELECT t FROM Tour t WHERE t.maxSlot > t.currentSlot")
    List<Tour> findAvaliableTours();

    // finddate+title
    List<Tour> findByTitleContainingIgnoreCaseAndStartDate(String title, LocalDate date);

    List<Tour> findByTitleContainingIgnoreCaseAndCategory_NameContainingIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            String title, String category, LocalDate d1, LocalDate d2);

    // title + date
    List<Tour> findByTitleContainingIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            String title, LocalDate d1, LocalDate d2);

    // category + date
    List<Tour> findByCategory_NameContainingIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            String category, LocalDate d1, LocalDate d2);

    // chỉ date
    List<Tour> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LocalDate d1, LocalDate d2);
  
   
}