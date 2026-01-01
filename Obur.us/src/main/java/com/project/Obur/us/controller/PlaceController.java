package com.project.Obur.us.controller;
import com.project.Obur.us.model.dto.PlaceDTO;
import com.project.Obur.us.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping
    @Operation(summary = "Yakındaki mekanları getir")
    public ResponseEntity<List<PlaceDTO>> getNearbyPlaces(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "1500") Double radius,
            @RequestParam(defaultValue = "50") Integer limit) {
        return ResponseEntity.ok(placeService.findNearbyPlaces(lat, lng, radius, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlaceById(@PathVariable Long id) {
        return ResponseEntity.ok(placeService.findById(id));
    }
}