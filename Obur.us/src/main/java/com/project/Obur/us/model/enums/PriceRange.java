package com.project.Obur.us.model.enums;

import lombok.Getter;

@Getter
public enum PriceRange {
    // Fiyat aralıkları dolar simgesiyle standartlaştırıldı.
    LOW("$"),
    MEDIUM("$$"),
    HIGH("$$$"),
    VERY_HIGH("$$$$");

    private final String symbol;

    PriceRange(String symbol) {
        this.symbol = symbol;
    }

    // JSON'a string olarak serileştirildiğinde sembolü döndürmek için
    @Override
    public String toString() {
        return symbol;
    }
}