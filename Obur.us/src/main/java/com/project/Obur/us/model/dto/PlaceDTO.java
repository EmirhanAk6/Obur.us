package com.project.Obur.us.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceDTO implements Serializable {

    private Long id;
    private String name;
    private String address;
    private String categories;

    @JsonProperty("rating_avg")
    private Double ratingAvg;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    // --- YENİ EKLENEN ALANLAR ---

    @JsonProperty("price_range")
    private Integer priceRange;

    @JsonProperty("reviews_text")
    private String reviewsText;

    private String source;
    private Double lat;
    private Double lng;

    @JsonProperty("distance_m")
    private Double distanceM;
}