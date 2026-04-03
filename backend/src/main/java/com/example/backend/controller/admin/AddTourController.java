package com.example.backend.controller.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import  org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.entity.Tour;
import com.example.backend.repository.TourRepository;
import com.example.backend.service.BookingService;
import com.example.backend.service.TourService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@RestController
@RequestMapping("/api/admin/tours")
@CrossOrigin(origins = "http://localhost:3000")
public class AddTourController {
    @Autowired
    private TourService tourService;
    @Autowired 
    private BookingService bookingService;
    @Autowired
    private TourRepository tourRepository;
    @GetMapping
    public List<Tour> getAll() {
        return tourService.getAllTour();
    }

    // add new tour
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/category/{categoryId}", consumes = {"multipart/form-data"})
        public ResponseEntity<Tour> create(
                @RequestPart("tour") String tourJson, 
                @RequestPart("images") List<MultipartFile> files,
                @PathVariable Long categoryId) throws IOException {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Tour tour = objectMapper.readValue(tourJson, Tour.class);

         
            List<String> imageUrls = new ArrayList<>();
            String uploadDir = "uploads/"; 
            
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    
                    imageUrls.add(uploadDir + fileName);
                }
            }
            tour.setImage(imageUrls);
            return ResponseEntity.ok(tourService.creaTour(tour, categoryId));
        }
    //update tour
        @PreAuthorize("hasRole('ADMIN')")
        @PutMapping(value = "/{id}/category/{categoryId}", consumes = {"multipart/form-data"})
        public ResponseEntity<Tour> update(
                @PathVariable Long id,
                @PathVariable Long categoryId,
                @RequestPart("tour") String tourJson,
                @RequestPart(value = "images", required = false) List<MultipartFile> files) throws IOException {

            // 1. Chuyển đổi JSON string sang Object Tour
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Tour tourDetails = objectMapper.readValue(tourJson, Tour.class);

            // 2. Tìm tour cũ trong DB
           Tour tour = tourRepository.findById(id).orElseThrow(() -> new RuntimeException("Lỗi..."));

            // 3. Xử lý hình ảnh
            List<String> imageUrls = new ArrayList<>();
            
            // Nếu người dùng có gửi ảnh mới lên
            if (files != null && !files.isEmpty() && !files.get(0).isEmpty()) {
                String uploadDir = "uploads/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                for (MultipartFile file : files) {
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    imageUrls.add(uploadDir + fileName);
                }
                tourDetails.setImage(imageUrls);
            } else {
                tourDetails.setImage(tour.getImage());
            }

            // 4. Gọi service để cập nhật
            Tour updatedTour = tourService.updateTour(id, tourDetails, categoryId);
            
            return ResponseEntity.ok(updatedTour);
        }

    // update number
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/slot")
    public ResponseEntity<Tour> UpdateSlot(@PathVariable Long id, @RequestParam int seats) {
        return ResponseEntity.ok(tourService.UpdateCurrentSlots(id, seats));
    }
    // confirm
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/confirm/{id}")
    public ResponseEntity<String> confirmTour(@PathVariable Long id){
        bookingService.confirmBooking(id);
        return ResponseEntity.ok("Xác nhận tour thành công");
    }
    // delete
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteTour(@PathVariable Long id) {
        tourService.deleteTour(id);
        return ResponseEntity.ok("Đã xóa tour thành công");
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admindelete/{id}")
    public ResponseEntity<String> deleteAdminTourConfirm(@PathVariable Long id){
        try{
            bookingService.AdminDeleteTourConfirm(id);
            return ResponseEntity.ok("Xóa tour sau khi xác nhận thành công");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    

}
