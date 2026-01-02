package com.project.Obur.us.controller;

import com.project.Obur.us.model.dto.ReviewDTO;
import com.project.Obur.us.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "NLP Destekli Yorum Yönetimi")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Mekana yorum ekle (NLP verisini tetikler)")
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO reviewDto) {
        return ResponseEntity.ok(reviewService.addReview(reviewDto));
    }

    @GetMapping("/place/{placeId}")
    @Operation(summary = "Mekanın tüm yorumlarını getir")
    public ResponseEntity<List<ReviewDTO>> getPlaceReviews(@PathVariable Long placeId) {
        return ResponseEntity.ok(reviewService.getReviewsByPlace(placeId));
    }
}