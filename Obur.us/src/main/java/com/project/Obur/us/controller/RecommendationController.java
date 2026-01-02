package com.project.Obur.us.controller;

import com.project.Obur.us.model.dto.PlaceDTO;
import com.project.Obur.us.service.PlaceService;
import com.project.Obur.us.service.RecommenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Recommendations", description = "Personalized restaurant recommendation endpoints")
public class RecommendationController {

    private final PlaceService placeService;
    private final RecommenderService recommenderService;

    @GetMapping
    @Operation(summary = "Get personalized recommendations")
    public ResponseEntity<Map<String, Object>> getRecommendations(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "5.0") @Min(1) @Max(50) Double radiusKm,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) Integer topK
    ) {
        log.info("Fetching recommendations for lat: {}, lng: {}, userId: {}", lat, lng, userId);

        try {
            // 1. PostGIS ile adayları getir (Metre cinsinden: radiusKm * 1000)
            List<PlaceDTO> candidates = placeService.findNearbyPlaces(lat, lng, radiusKm * 1000, 200);

            if (candidates.isEmpty()) {
                return ResponseEntity.ok(Map.of("message", "No candidates found", "items", List.of()));
            }

            // 2. Python /rank endpoint'ine gönder
            Map<String, Object> recommendations = recommenderService.getRecommendations(userId, lat, lng, candidates);
            return ResponseEntity.ok(recommendations);

        } catch (Exception e) {
            log.error("Error in general recommendations", e);
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Get user-specific historical recommendations")
    public ResponseEntity<Map<String, Object>> getUserRecommendations(
            @PathVariable Long userId,
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5.0") Double radiusKm,
            @RequestParam(defaultValue = "10") Integer topK,
            @RequestParam(required = false) Integer priceRange,
            @RequestParam(required = false) String prefs
    ) {
        try {
            // RecommenderService içindeki getUserRecommendations metodunu çağırır
            Map<String, Object> result = recommenderService.getUserRecommendations(
                    userId, lat, lng, radiusKm, topK, priceRange, prefs);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error in user recommendations", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to fetch user recommendations"));
        }
    }
}