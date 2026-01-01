package com.project.Obur.us.controller;

import com.project.Obur.us.model.dto.PlaceDTO;
import com.project.Obur.us.service.PlaceService;
import com.project.Obur.us.service.RecommenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Validated // Bean validation'ların çalışması için gerekli
@Tag(name = "Recommendations", description = "Kişiselleştirilmiş restoran öneri endpoint'leri")
public class RecommendationController {

    private final PlaceService placeService;
    private final RecommenderService recommenderService;

    @GetMapping
    @Operation(summary = "Genel önerileri al (Hibrit Sıralama)",
            description = "Konum bazlı adayları PostGIS ile bulur ve Python servisine sıralatır.")
    public ResponseEntity<Map<String, Object>> getRecommendations(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "5.0") @Min(1) @Max(50) Double radiusKm,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) Integer topK
    ) {
        log.info("Öneri isteği: lat={}, lng={}, userId={}, radius={}km", lat, lng, userId, radiusKm);

        try {
            // 1. PostGIS kullanarak yakındaki aday mekanları çekiyoruz
            // Onur'un api/src/index.js dosyasındaki /places mantığına benzer
            List<PlaceDTO> candidates = placeService.findNearbyPlaces(
                    lat, lng, radiusKm * 1000, 200
            );

            if (candidates.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "message", "Bu bölgede uygun mekan bulunamadı",
                        "items", List.of()
                ));
            }

            // 2. Adayları Python servisine (/rank) gönderip sıralatıyoruz
            Map<String, Object> recommendations = recommenderService.getRecommendations(
                    userId, lat, lng, candidates
            );

            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            log.error("Öneri alınırken hata oluştu", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Öneri servisi şu an yanıt vermiyor",
                    "details", e.getMessage()
            ));
        }
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Kullanıcıya özel geçmiş bazlı öneriler",
            description = "Graph tabanlı (Neo4j) kişiselleştirilmiş önerileri getirir.")
    public ResponseEntity<Map<String, Object>> getUserRecommendations(
            @PathVariable Long userId,
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5.0") Double radiusKm,
            @RequestParam(defaultValue = "10") Integer topK,
            @RequestParam(required = false) @Min(1) @Max(4) Integer priceRange,
            @RequestParam(required = false) String prefs
    ) {
        log.info("Kullanıcıya özel öneri isteği: userId={}, prefs={}", userId, prefs);

        try {
            // Python servisindeki /users/{user_id}/recommendations endpoint'ini çağırır
            Map<String, Object> recommendations = recommenderService.getUserRecommendations(
                    userId, lat, lng, radiusKm, topK, priceRange, prefs
            );

            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            log.error("Kullanıcı önerisi alınırken hata", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Kullanıcı verileri işlenirken hata oluştu"
            ));
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Recommender servis durumunu kontrol et")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        // Python servisinin /health endpoint'ine bakar
        boolean healthy = recommenderService.isRecommenderHealthy();
        return ResponseEntity.ok(Map.of(
                "recommenderStatus", healthy ? "UP" : "DOWN",
                "timestamp", System.currentTimeMillis()
        ));
    }
}
