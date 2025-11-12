package com.project.Obur.us.service;

import com.project.Obur.us.dto.AiRecommendationRequest;
import com.project.Obur.us.dto.AiRecommendationResponse;
import com.project.Obur.us.dto.RecommendationResponseDto;
import com.project.Obur.us.persistence.entity.Place;
import com.project.Obur.us.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final PlaceRepository placeRepository;
    private final WebClient webClient; // WebClient, Config dosyasında Bean olarak tanımlanmalıdır.

    // AI Servisi URL'si (örneğin application.properties'den çekilir)
    // @Value("${ai.service.url}")
    // private String aiServiceUrl;

    // Not: WebClient Bean olarak enjekte edildiği için baseUrl ayarı Config'de yapılmalıdır.

    /**
     * AI Servisini tetikleyerek kişiselleştirilmiş öneri listesini çeker.
     * Bu metot, Protoko 1'i (Senkron AI Çağrısı) uygular.
     */
    public List<RecommendationResponseDto> getRecommendations(Long userId, double lat, double lon, double radius) {

        // 1. AI Servisine Gönderilecek İstek Hazırlığı (Past interactions ve Context verileri)
        AiRecommendationRequest request = buildAiRequest(userId, lat, lon, radius);

        // 2. KRİTİK: Senkron REST Çağrısı (WebClient)
        AiRecommendationResponse aiResponse = webClient.post()
                .uri("/ai/recommendations/calculate") // AI Servisindeki endpoint
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AiRecommendationResponse.class)
                .block(); // Senkron çalışması için bloklama.

        // Eğer AI'dan sonuç gelmezse veya null ise boş liste dönülür.
        if (aiResponse == null || aiResponse.getRecommendedPlaces() == null || aiResponse.getRecommendedPlaces().isEmpty()) {
            return new ArrayList<>();
        }

        // 3. Skorlu ID Listesi Hazırlığı ve Map'leme
        List<Long> scoredPlaceIds = aiResponse.getRecommendedPlaces().stream()
                .map(p -> p.getPlaceId())
                .collect(Collectors.toList());

        // Skorları hızlı erişim için Map'e dönüştürme
        Map<Long, Double> scoreMap = aiResponse.getRecommendedPlaces().stream()
                .collect(Collectors.toMap(
                        p -> p.getPlaceId(),
                        p -> p.getRecommendationScore()
                ));


        // 4. DB'den Detay Çekme (PlaceRepository)
        List<Place> places = placeRepository.findAllByIdIn(scoredPlaceIds);


        // 5. Frontend DTO'ya Dönüştürme ve Skor Entegrasyonu
        return places.stream()
                .map(place -> convertToDto(place, scoreMap.get(place.getId())))
                .collect(Collectors.toList());
    }

    /**
     * AI Servisine gönderilecek isteği hazırlar.
     * Not: Gerçek uygulamada past_interactions ve Context verileri DB'den/Hava Durumu API'lerinden çekilmelidir.
     */
    private AiRecommendationRequest buildAiRequest(Long userId, double lat, double lon, double radius) {

        // Konum DTO'su
        AiRecommendationRequest.Location location =
                new AiRecommendationRequest.Location(lat, lon, radius);

        // Bağlam DTO'su (Mock veriler)
        AiRecommendationRequest.Context context =
                new AiRecommendationRequest.Context("Wednesday", "Lunch", "Sunny");

        // Geçmiş etkileşimler (Şu an boş liste - DB'den çekilmelidir)
        List<AiRecommendationRequest.PastInteraction> pastInteractions =
                new ArrayList<>();

        // Ana Request DTO'sunu döndürme
        return new AiRecommendationRequest(userId, location, context, pastInteractions);
    }

    /**
     * Place Entity'den RecommendationResponseDto'ya dönüşüm yapar ve skoru ekler.
     */
    private RecommendationResponseDto convertToDto(Place place, Double score) {
        RecommendationResponseDto dto = new RecommendationResponseDto();
        dto.setId(place.getId());
        dto.setName(place.getName());
        dto.setCuisine(place.getCuisine().name());
        dto.setPriceRange(place.getPriceRange().name());
        dto.setRecommendationScore(score);
        // Not: Ortalama rating ve mesafe hesaplama mantığı burada eklenmelidir.
        return dto;
    }
}