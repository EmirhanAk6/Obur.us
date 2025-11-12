package com.project.Obur.us.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "reviews")
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float rating; // 1.0 - 5.0 arası puan

    @Column(columnDefinition = "TEXT")
    private String comment; // NLP için metin verisi

    // İlişki: Review'in sahibi (User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // İlişki: Review'in yapıldığı mekan (Place)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private Place place;

    // Getter ve Setter'lar buraya eklenmeli
}
