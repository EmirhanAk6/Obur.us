package com.project.Obur.us.service;

import com.project.Obur.us.model.entity.Review;
import com.project.Obur.us.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional
    public Review addReview(Review review) {
        // PostgreSQL'e kaydet
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByPlace(Long placeId) {
        return reviewRepository.findByPlaceIdOrderByCreatedAtDesc(placeId);
    }
}