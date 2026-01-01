package com.project.Obur.us.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationResponse implements Serializable {

    private String algo;

    @JsonProperty("input_prefs")
    private List<String> inputPrefs;

    @JsonProperty("expanded_prefs")
    private List<String> expandedPrefs;

    @JsonProperty("seed_restaurant_id")
    private Long seedRestaurantId;

    private Integer count;
    private List<RecommendedItem> items;
}

