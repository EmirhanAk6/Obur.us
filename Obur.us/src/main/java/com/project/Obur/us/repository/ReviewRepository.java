package com.project.Obur.us.repository;

import com.project.Obur.us.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Bir mekana ait yorumları en yeniden en eskiye listeler
    List<Review> findByPlaceIdOrderByCreatedAtDesc(Long placeId);
}
