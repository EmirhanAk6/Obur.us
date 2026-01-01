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
        return placeRepository.findNearbyPlaces(lat, lng, radius, limit).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private PlaceDTO mapToDTO(Place place) {
        return PlaceDTO.builder()
                .id(place.getId())
                .name(place.getName())
                .address(place.getAddress())
                .categories(place.getCategories())
                .ratingAvg(place.getRatingAvg())
                .ratingCount(place.getRatingCount())
                .source(place.getSource())
                .build();
    }

    public Place findById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mekan bulunamadı: " + id));
    }
}
