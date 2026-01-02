package com.project.Obur.us.service;

import com.project.Obur.us.model.dto.ReviewDTO;
import com.project.Obur.us.model.entity.Place;
import com.project.Obur.us.model.entity.Review;
import com.project.Obur.us.model.entity.User;
import com.project.Obur.us.repository.PlaceRepository;
import com.project.Obur.us.repository.ReviewRepository;
import com.project.Obur.us.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewDTO addReview(ReviewDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        Place place = placeRepository.findById(dto.getPlaceId())
                .orElseThrow(() -> new RuntimeException("Mekan bulunamadı"));

        // 1. PostgreSQL'e kaydet
        Review review = Review.builder()
                .user(user)
                .place(place)
                .rating(dto.getRating())
                .text(dto.getText())
                .build();

        Review savedReview = reviewRepository.save(review);

        // 2. KRİTİK: Place tablosundaki reviews_text alanını güncelle (NLP için)
        // Yeni yorumu mekanın mevcut yorum metinlerine ekliyoruz.
        String updatedReviewsText = (place.getReviewsText() == null ? "" : place.getReviewsText() + " ") + dto.getText();
        place.setReviewsText(updatedReviewsText);

        // Ortalama puanı da güncelle
        double newAvg = ((place.getRatingAvg() * place.getRatingCount()) + dto.getRating()) / (place.getRatingCount() + 1);
        place.setRatingAvg(newAvg);
        place.setRatingCount(place.getRatingCount() + 1);

        placeRepository.save(place);

        log.info("Yeni yorum eklendi ve mekan NLP verisi güncellendi. PlaceId: {}", place.getId());

        return mapToDTO(savedReview);
    }

    public List<ReviewDTO> getReviewsByPlace(Long placeId) {
        return reviewRepository.findByPlaceIdOrderByCreatedAtDesc(placeId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private ReviewDTO mapToDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .placeId(review.getPlace().getId())
                .rating(review.getRating())
                .text(review.getText())
                .createdAt(review.getCreatedAt())
                .build();
    }
}