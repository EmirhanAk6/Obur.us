package com.project.Obur.us.controller;

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
@Tag(name = "Health", description = "Application health check endpoints")
public class HealthController {

    @GetMapping
    @Operation(summary = "Basic health check")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "service", "oburus-api-spring",
                "version", "1.0.0",
                "timestamp", LocalDateTime.now()
        ));
    }

    @GetMapping("/ready")
    @Operation(summary = "Readiness probe")
    public ResponseEntity<Map<String, Object>> ready() {
        return ResponseEntity.ok(Map.of(
                "status", "ready",
                "timestamp", LocalDateTime.now()
        ));
    }

    @GetMapping("/live")
    @Operation(summary = "Liveness probe")
    public ResponseEntity<Map<String, Object>> live() {
        return ResponseEntity.ok(Map.of(
                "status", "alive",
                "timestamp", LocalDateTime.now()
        ));
    }
}
