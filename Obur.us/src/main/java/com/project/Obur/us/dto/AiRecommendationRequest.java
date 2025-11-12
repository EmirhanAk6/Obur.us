package com.project.Obur.us.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiRecommendationRequest {

    private Long userId;
    private Location location;
    private Context context;
    private List<PastInteraction> pastInteractions;

    // İç Sınıflar: Lombok ile basit yapılar
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Location {
        private double lat;
        private double lon;
        private double radiusKm;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Context {
        private String dayOfWeek;
        private String timeOfDay;
        private String weather;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PastInteraction {
        private Long placeId;
        private int rating;
    }
}