package com.project.Obur.us.model.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private Long userId;
    private Long placeId;
    private Double rating;
    private String text;
    private OffsetDateTime createdAt;
}
