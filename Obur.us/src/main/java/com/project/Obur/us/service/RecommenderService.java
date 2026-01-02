package com.project.Obur.us.service;

import com.project.Obur.us.model.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
     * Python /rank endpoint'ine adayları gönderir ve sıralanmış sonucu DTO olarak döner.
     */
    public RecommendationResponse getRecommendations(
            String userId,
            Double lat, Double lng,
            List<PlaceDTO> candidates
    ) {
        log.debug("Adaylar Python'a gönderiliyor: user={}, count={}", userId, candidates.size());

        // Aday listesini Python servisinin performansı için kısıtlıyoruz
        List<Map<String, Object>> candidateList = candidates.stream()
                .limit(maxCandidates)
                .map(this::convertToMapWithNLP)
                .collect(Collectors.toList());

        // Python tarafındaki pydantic modeline (RecommendationRequest) uygun body
        RecommendationRequest request = RecommendationRequest.builder()
                .userId(userId)
                .lat(lat)
                .lng(lng)
                .candidates(candidateList)
                .build();

        try {
            return recommenderWebClient
                    .post()
                    .uri("/rank")
                    .body(Mono.just(request), RecommendationRequest.class)
                    .retrieve()
                    .bodyToMono(RecommendationResponse.class) // Map yerine DTO kullanıldı
                    .block();
        } catch (Exception e) {
            log.error("Sıralama servisi hatası", e);
            throw new RuntimeException("Öneri sıralaması yapılamadı: " + e.getMessage());
        }
    }

    /**
     * Neo4j tabanlı kişiselleştirilmiş önerileri DTO olarak döner.
     */
    public RecommendationResponse getUserRecommendations(
            Long userId, Double lat, Double lng,
            Double radiusKm, Integer topK, Integer priceRange, String prefs
    ) {
        try {
            return recommenderWebClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/users/{userId}/recommendations")
                            .queryParam("lat", lat)
                            .queryParam("lng", lng)
                            .queryParam("radius_km", radiusKm != null ? radiusKm : 5.0)
                            .queryParam("topK", topK != null ? topK : 10)
                            .queryParamIfPresent("price_range", java.util.Optional.ofNullable(priceRange))
                            .queryParamIfPresent("prefs", java.util.Optional.ofNullable(prefs))
                            .build(userId))
                    .retrieve()
                    .bodyToMono(RecommendationResponse.class) // Map yerine DTO kullanıldı
                    .block();
        } catch (Exception e) {
            log.error("Kişiselleştirilmiş öneri hatası", e);
            throw new RuntimeException("Kullanıcı graf önerisi başarısız.");
        }
    }

    private Map<String, Object> convertToMapWithNLP(PlaceDTO place) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", place.getId());
        map.put("name", place.getName());
        map.put("lat", place.getLat());
        map.put("lng", place.getLng());
        map.put("categories", place.getCategories());
        map.put("rating_avg", place.getRatingAvg());
        map.put("rating_count", place.getRatingCount());
        map.put("price_range", place.getPriceRange());
        map.put("reviews_text", place.getReviewsText());
        return map;
    }

    public boolean isRecommenderHealthy() {
        try {
            Map response = recommenderWebClient.get().uri("/health").retrieve().bodyToMono(Map.class).block();
            return response != null && (Boolean.TRUE.equals(response.get("ok")) || "ok".equals(response.get("status")));
        } catch (Exception e) { return false; }
    }
}