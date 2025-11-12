package com.project.Obur.us.service;

import com.project.Obur.us.dto.ReviewRequestDto;
import com.project.Obur.us.persistence.entity.Review;
import com.project.Obur.us.repository.ReviewRepository;
import com.project.Obur.us.repository.UserRepository;
import com.project.Obur.us.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final WebClient webClient;

    // Kritik Metod: Yorumu kaydet ve AI'yı bilgilendir
    public void saveAndNotifyAi(ReviewRequestDto reviewDto, Long userId) {

        // 1. DTO'dan Entity'ye Dönüşüm
        var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        var place = placeRepository.findById(reviewDto.getPlaceId()).orElseThrow(() -> new RuntimeException("Place not found"));

        Review review = new Review();
        // ... set diğer alanlar
        review.setUser(user);
        review.setPlace(place);
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());

        Review savedReview = reviewRepository.save(review);

        // 2. AI'ya Asenkron Bildirim (Protokol 2)
        notifyAiForFeedback(savedReview);
    }

    @Async // KRİTİK: Asenkron çalışması için @EnableAsync annotation'ı gereklidir.
    public void notifyAiForFeedback(Review review) {

        // Protokol 2 formatında JSON hazırlığı
        Object aiFeedbackData = Map.of(
                "user_id", review.getUser().getId(),
                "place_id", review.getPlace().getId(),
                "rating", review.getRating(),
                "comment", review.getComment()
        );

        webClient.post()
                .uri("/ai/feedback/new-interaction")
                .bodyValue(aiFeedbackData)
                .retrieve()
                .toBodilessEntity()
                .subscribe(
                        null,
                        error -> System.err.println("Failed to notify AI Service: " + error.getMessage())
                );
    }
}
