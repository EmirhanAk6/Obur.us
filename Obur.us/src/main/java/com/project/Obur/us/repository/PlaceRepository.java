package com.project.Obur.us.repository;

import com.project.Obur.us.persistence.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    // AI'dan gelen skorlu ID'lere ait tüm detayları çekmek için
    List<Place> findAllByIdIn(List<Long> placeIds);

    // PostGIS ile coğrafi sorgu (Harita Görünümü için)
    @Query(value = "SELECT p FROM Place p WHERE function('ST_Contains', :boundingBox, p.locationGeo) = true")
    List<Place> findPlacesWithinBounds(@Param("boundingBox") String boundingBox);
}
