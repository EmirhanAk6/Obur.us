package com.project.Obur.us.controller;

import com.project.Obur.us.dto.RecommendationResponseDto;
import com.project.Obur.us.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.project.Obur.us.persistence.entity.User; // UserDetails'in User'a cast edildiği varsayımı

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<List<RecommendationResponseDto>> getRecommendations(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "5") double radius_km
    ) {
        // KRİTİK: JWT'den alınan gerçek User ID
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = currentUser.getId();

        List<RecommendationResponseDto> recommendations =
                recommendationService.getRecommendations(userId, lat, lon, radius_km);

        return ResponseEntity.ok(recommendations);
    }

    // Harita verisi sorgusu (PostGIS)
    @GetMapping("/map-data")
    public ResponseEntity<?> getMapData(
            // Koordinat parametreleri...
    ) {
        // PostGIS sorgusunun Service katmanında çalıştırılması.
        return ResponseEntity.ok("PostGIS Map Data Endpoint is operational.");
    }
}
