package com.project.Obur.us.controller;

import com.project.Obur.us.model.dto.PlaceDTO;
import com.project.Obur.us.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
@Tag(name = "Places", description = "Mekan yönetimi ve arama")
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping
    @Operation(summary = "Yakındaki mekanları getir (PostGIS tabanlı)")
    public ResponseEntity<List<PlaceDTO>> getNearbyPlaces(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "1500.0") Double radius,
            @RequestParam(defaultValue = "50") Integer limit) {
        // Bu endpoint, RecommendationController'daki hibrit sıralama için "aday" listesini sağlar.
        return ResponseEntity.ok(placeService.findNearbyPlaces(lat, lng, radius, limit));
    }

    @GetMapping("/{id}")
    @Operation(summary = "ID ile mekan detayı getir")
    public ResponseEntity<PlaceDTO> getPlaceById(@PathVariable Long id) {
        // mapToDTO metodunu kullanan Service katmanına yönlendirme yapıyoruz
        return ResponseEntity.ok(placeService.getPlaceDetails(id));
    }
}