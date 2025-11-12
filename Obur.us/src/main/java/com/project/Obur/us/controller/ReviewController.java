package com.project.Obur.us.controller;

import com.project.Obur.us.dto.ReviewRequestDto;
import com.project.Obur.us.service.ReviewService;
import com.project.Obur.us.persistence.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<String> submitReview(@RequestBody ReviewRequestDto reviewDto) {

        // KRİTİK: JWT'den alınan gerçek User ID
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = currentUser.getId();

        try {
            reviewService.saveAndNotifyAi(reviewDto, userId);
            return new ResponseEntity<>("Review recorded and AI notified.", HttpStatus.CREATED);
        } catch (Exception e) {
            // Detaylı loglama ve Hata Yönetimi
            return new ResponseEntity<>("Failed to record review.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}