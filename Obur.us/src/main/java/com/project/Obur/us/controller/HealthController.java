package com.project.Obur.us.controller;

import com.project.Obur.us.service.RecommenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Health", description = "Uygulama sağlık kontrolü")
public class HealthController {

    private final RecommenderService recommenderService;

    @GetMapping
    @Operation(summary = "Genel sağlık durumu ve servis bağımlılıkları")
    public ResponseEntity<Map<String, Object>> health() {
        // Python servisinin durumunu kontrol et
        boolean recommenderUp = recommenderService.isRecommenderHealthy();

        return ResponseEntity.ok(Map.of(
                "ok", recommenderUp,
                "service", "oburus-api-spring",
                "recommenderStatus", recommenderUp ? "UP" : "DOWN",
                "timestamp", LocalDateTime.now()
        ));
    }

    @GetMapping("/ready")
    @Operation(summary = "Hazır olma kontrolü (Readiness)")
    public ResponseEntity<Map<String, Object>> ready() {
        return ResponseEntity.ok(Map.of(
                "status", "ready",
                "timestamp", LocalDateTime.now()
        ));
    }
}