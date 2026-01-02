package com.project.Obur.us.service;

import com.project.Obur.us.model.dto.PlaceDTO;
import com.project.Obur.us.model.entity.Place;
import com.project.Obur.us.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private final PlaceRepository placeRepository;

    public List<PlaceDTO> findNearbyPlaces(Double lat, Double lng, Double radius, Integer limit) {
        // PostGIS sorgusundan gelen mekanları DTO'ya dönüştürerek listeler
        return placeRepository.findNearbyPlaces(lat, lng, radius, limit).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Tek bir mekanın detaylarını DTO olarak döner.
     * Controller katmanındaki getPlaceById isteği için gereklidir.
     */
    public PlaceDTO getPlaceDetails(Long id) {
        Place place = placeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mekan bulunamadı: " + id));
        return mapToDTO(place);
    }

    /**
     * Entity'den DTO'ya dönüşüm.
     * NLP (reviewsText) ve Fiyat Aralığı (priceRange) desteği eklenmiştir.
     */
    private PlaceDTO mapToDTO(Place place) {
        return PlaceDTO.builder()
                .id(place.getId())
                .name(place.getName())
                .address(place.getAddress())
                .categories(place.getCategories())
                .ratingAvg(place.getRatingAvg())
                .ratingCount(place.getRatingCount())
                .source(place.getSource())
                .lat(place.getLatitude())  // JTS Point'ten Latitude çekilir
                .lng(place.getLongitude()) // JTS Point'ten Longitude çekilir
                .distanceM(place.getDistanceM()) // PostGIS distance_m alias eşleşmesi
                .priceRange(place.getPriceRange()) // Onur'un yeni fiyat seviyesi alanı
                .reviewsText(place.getReviewsText()) // NLP analizi için kritik ham metin
                .build();
    }

    public Place findById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mekan bulunamadı: " + id));
    }
}