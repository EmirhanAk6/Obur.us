package com.project.Obur.us.repository;

import com.project.Obur.us.model.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query(value = """
        SELECT *, 
               ST_Distance(location_geo, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography) as distance_m
        FROM places 
        WHERE ST_DWithin(
            location_geo, 
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography, 
            :radius
        )
        ORDER BY distance_m ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Place> findNearbyPlaces(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radius") Double radius,
            @Param("limit") Integer limit);
}