package com.project.Obur.us.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;
import java.io.Serializable;

@Entity
@Table(name = "places", indexes = {
        @Index(name = "ix_places_location_geo", columnList = "location_geo")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Place implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "location_geo", nullable = false, columnDefinition = "geography(Point,4326)")
    private Point locationGeo;

    private String address;

    private String categories;

    @Column(name = "rating_avg")
    private Double ratingAvg;

    @Column(name = "rating_count")
    private Integer ratingCount;

    // --- YENİ EKLENEN ALANLAR (Onur'un projesiyle uyum için) ---

    @Column(name = "price_range")
    private Integer priceRange; // 1, 2, 3, 4 şeklinde fiyat seviyesi

    @Column(name = "reviews_text", columnDefinition = "TEXT")
    private String reviewsText; // NLP/Sentiment analizi için ham yorum verisi

    private String source;

    @Transient // Veritabanı tablosunda fiziksel olarak yoktur
    private Double distanceM;

    @Transient
    private Double distanceM;

    @Transient
    public Double getLatitude() {
        return locationGeo != null ? locationGeo.getY() : null;
    }

    @Transient
    public Double getLongitude() {
        return locationGeo != null ? locationGeo.getX() : null;
    }
}