package com.project.Obur.us.model.dto;

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
public class RecommendationRequest implements Serializable {

    private String userId;
    private Double lat;
    private Double lng;
    private List<Map<String, Object>> candidates;
}

