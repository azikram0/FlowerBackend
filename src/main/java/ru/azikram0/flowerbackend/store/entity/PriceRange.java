package ru.azikram0.flowerbackend.store.entity;

import java.math.BigDecimal;

public record PriceRange(BigDecimal min, BigDecimal max) {
}
