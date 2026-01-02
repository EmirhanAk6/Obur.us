package com.project.Obur.us.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point; // ÖNEMLİ: java.awt.Point DEĞİL, JTS Point kullanılmalı
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

    // PostGIS geography tipi için JTS kütüphanesinden Point kullanılmalıdır
    @Column(name = "location_geo", nullable = false, columnDefinition = "geography(Point,4326)")
    private Point locationGeo;

    private String address;

    private String categories;

    @Column(name = "rating_avg")
    private Double ratingAvg;

    @Column(name = "rating_count")
    private Integer ratingCount;

    private String source;

    // --- KRİTİK EKLEME: Mesafe bilgisini tutacak alan ---
    // PlaceRepository içindeki "as distance_m" sonucunun buraya dolması için gereklidir
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
