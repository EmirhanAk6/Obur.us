package com.project.Obur.us.service;

import com.project.Obur.us.model.dto.PlaceDTO;
import com.project.Obur.us.model.dto.RecommendationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommenderService {

    private final WebClient recommenderWebClient;

    @Value("${recommender.max-candidates:200}")
    private int maxCandidates;

    /**
     * Get recommendations from Python recommender service
     */
    public Map<String, Object> getRecommendations(
            String userId,
            Double lat,
            Double lng,
            List<PlaceDTO> candidates
    ) {
        log.debug("Requesting recommendations for user: {}, location: ({}, {}), candidates: {}",
                userId, lat, lng, candidates.size());

        List<Map<String, Object>> candidateList = candidates.stream()
                .limit(maxCandidates)
                .map(this::convertToMap)
                .collect(Collectors.toList());

        RecommendationRequest request = RecommendationRequest.builder()
                .userId(userId)
                .lat(lat)
                .lng(lng)
                .candidates(candidateList)
                .build();

        try {
            Map<String, Object> response = recommenderWebClient
                    .post()
                    .uri("/rank")
                    .body(Mono.just(request), RecommendationRequest.class)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            log.debug("Received {} recommendations",
                    response != null ? ((List<?>) response.get("items")).size() : 0);

            return response;
        } catch (Exception e) {
            log.error("Error calling recommender service", e);
            throw new RuntimeException("Failed to get recommendations: " + e.getMessage(), e);
        }
    }

    /**
     * Get user-specific recommendations with preferences
     */
    public Map<String, Object> getUserRecommendations(
            Long userId,
            Double lat,
            Double lng,
            Double radiusKm,
            Integer topK,
            Integer priceRange,
            String prefs
    ) {
        log.debug("Getting user recommendations: userId={}, location=({},{})", userId, lat, lng);

        try {
            Map<String, Object> response = recommenderWebClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/users/{userId}/recommendations")
                            .queryParam("lat", lat)
                            .queryParam("lng", lng)
                            .queryParam("radius_km", radiusKm)
                            .queryParam("topK", topK)
                            .queryParamIfPresent("price_range", java.util.Optional.ofNullable(priceRange))
                            .queryParamIfPresent("prefs", java.util.Optional.ofNullable(prefs))
                            .build(userId))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            log.debug("Received user recommendations: {}",
                    response != null ? ((List<?>) response.get("items")).size() : 0);

            return response;
        } catch (Exception e) {
            log.error("Error getting user recommendations", e);
            throw new RuntimeException("Failed to get user recommendations: " + e.getMessage(), e);
        }
    }

    /**
     * Convert PlaceDTO to Map for recommender service
     */
    private Map<String, Object> convertToMap(PlaceDTO place) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", place.getId());
        map.put("name", place.getName());
        map.put("lat", place.getLat());
        map.put("lng", place.getLng());
        map.put("categories", place.getCategories());
        map.put("rating_avg", place.getRatingAvg());
        map.put("rating_count", place.getRatingCount());
        return map;
    }

    /**
     * Health check for recommender service
     */
    public boolean isRecommenderHealthy() {
        try {
            Map<String, Object> response = recommenderWebClient
                    .get()
                    .uri("/health")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return response != null && Boolean.TRUE.equals(response.get("ok"));
        } catch (Exception e) {
            log.warn("Recommender service health check failed", e);
            return false;
        }
    }
}
