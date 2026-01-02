package com.project.Obur.us.controller;

import com.project.Obur.us.model.dto.*;
import com.project.Obur.us.service.*;
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
@Tag(name = "Recommendations", description = "Kişiselleştirilmiş restoran öneri endpoint'leri")
public class RecommendationController {

    private final PlaceService placeService;
    private final RecommenderService recommenderService;

    @GetMapping
    @Operation(summary = "Hibrit Sıralama Önerileri",
            description = "PostGIS adaylarını Python NLP ve hibrit motoruyla sıralar.")
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "5.0") @Min(1) @Max(50) Double radiusKm,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) Integer topK
    ) {
        log.info("Hibrit öneri isteği: lat={}, lng={}", lat, lng);

        List<PlaceDTO> candidates = placeService.findNearbyPlaces(lat, lng, radiusKm * 1000, 200);

        if (candidates.isEmpty()) {
            return ResponseEntity.ok(RecommendationResponse.builder()
                    .algo("none").count(0).items(List.of()).build());
        }

        RecommendationResponse response = recommenderService.getRecommendations(userId, lat, lng, candidates);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Graf Tabanlı Kişiselleştirilmiş Öneriler",
            description = "Kullanıcının geçmişine göre Neo4j üzerinden öneri getirir.")
    public ResponseEntity<RecommendationResponse> getUserRecommendations(
            @PathVariable Long userId,
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5.0") Double radiusKm,
            @RequestParam(defaultValue = "10") Integer topK,
            @RequestParam(required = false) Integer priceRange,
            @RequestParam(required = false) String prefs
    ) {
        log.info("Kullanıcı {} için graf önerisi başlatıldı", userId);
        RecommendationResponse response = recommenderService.getUserRecommendations(
                userId, lat, lng, radiusKm, topK, priceRange, prefs);
        return ResponseEntity.ok(response);
    }
}