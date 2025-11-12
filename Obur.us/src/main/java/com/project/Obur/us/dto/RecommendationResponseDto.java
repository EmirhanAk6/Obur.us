package com.project.Obur.us.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendationResponseDto {
    private Long id;
    private String name;
    private String cuisine;
    private String priceRange;
    private double rating;
    private double recommendationScore; // AI'dan gelen skor
    private double distanceKm;
}