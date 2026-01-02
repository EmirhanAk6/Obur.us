package com.project.Obur.us.service;

import com.project.Obur.us.model.dto.PlaceDTO;
import com.project.Obur.us.model.dto.RecommendationRequest;
import com.project.Obur.us.model.dto.RecommendationResponse;
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
     * Python Recommender servisinin /rank endpoint'ine (POST) adayları gönderir ve sıralatır.
     * Onur'un Python tarafındaki 'rank' fonksiyonuna uygun veri yapısını hazırlar.
     */
    public Map<String, Object> getRecommendations(
            String userId,
            Double lat,
            Double lng,
            List<PlaceDTO> candidates
    ) {
        log.debug("Python servisine sıralama isteği gönderiliyor. Aday sayısı: {}", candidates.size());

        // Aday listesini Python servisinin performansı için kısıtlıyoruz
        List<Map<String, Object>> candidateList = candidates.stream()
                .limit(maxCandidates)
                .map(this::convertToMap)
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
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            log.error("Recommender /rank servisi çağrılırken hata: ", e);
            throw new RuntimeException("Öneri sıralama işlemi başarısız: " + e.getMessage());
        }
    }

    /**
     * Kullanıcı geçmişine dayalı (Neo4j/Graph) öneriler için Python /users/{userId}/recommendations çağrısı yapar.
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
        log.debug("Graph tabanlı kullanıcı önerileri isteniyor: userId={}", userId);

        try {
            return recommenderWebClient
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
        } catch (Exception e) {
            log.error("Kullanıcıya özel öneriler alınırken hata: ", e);
            throw new RuntimeException("Kişiselleştirilmiş öneri servisi hatası: " + e.getMessage());
        }
    }

    /**
     * PlaceDTO nesnesini Python servisinin beklediği Map yapısına dönüştürür.
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
     * Recommender servisinin ayakta olup olmadığını kontrol eder.
     */
    public boolean isRecommenderHealthy() {
        try {
            Map<String, Object> response = recommenderWebClient
                    .get()
                    .uri("/health")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return response != null && "graph_hybrid_v3".equals(response.get("algo"));
        } catch (Exception e) {
            log.warn("Recommender servis sağlığı kontrol edilemedi.");
            return false;
        }
    }
}