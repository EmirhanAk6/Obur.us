package com.project.Obur.us.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendedItem implements Serializable {

    private Long id;
    private String name;
    private Double lat;
    private Double lng;
    private Double km;
    private String categories;

    @JsonProperty("rating_avg")
    private Double ratingAvg;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    @JsonProperty("price_range")
    private Integer priceRange;

    private Double pagerank;

    @JsonProperty("matched_categories")
    private List<String> matchedCategories;

    @JsonProperty("sentiment_score")
    private Double sentimentScore;

    @JsonProperty("sentiment_label")
    private String sentimentLabel;

    private Double score;

    @JsonProperty("score_breakdown")
    private Map<String, Double> scoreBreakdown;
}

