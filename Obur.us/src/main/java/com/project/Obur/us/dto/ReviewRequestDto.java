package com.project.Obur.us.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequestDto {
    private Long placeId;
    private Float rating;
    private String comment;
}
