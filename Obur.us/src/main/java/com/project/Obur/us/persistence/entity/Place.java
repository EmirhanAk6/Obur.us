package com.project.Obur.us.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point; // PostGIS için kullanılacak kütüphane
import java.util.List;
import com.project.Obur.us.model.enums.CuisineType;
import com.project.Obur.us.model.enums.PriceRange;
@Entity
@Table(name = "places")
@Getter
@Setter
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING) // DB'de string olarak saklanmasını sağlar
    private CuisineType cuisine;

    @Enumerated(EnumType.STRING)
    private PriceRange priceRange;

    // Kritik: PostGIS entegrasyonu için coğrafi konum
    @Column(columnDefinition = "geometry(Point, 4326)") // SRID 4326 (WGS 84)
    private Point locationGeo;

    // İlişki: Bir mekan birden çok yorum alabilir.
    @OneToMany(mappedBy = "place", cascade = CascadeType.ALL)
    private List<Review> reviews;

    // Getter ve Setter'lar buraya eklenmeli
}
