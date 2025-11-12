package com.project.Obur.us.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class AiRecommendationResponse {

    private String status;
    private List<ScoredPlace> recommendedPlaces;

    @Getter
    @Setter
    public static class ScoredPlace {
        private Long placeId;
        private Double recommendationScore;
    }
}