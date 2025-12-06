package ru.azikram0.flowerbackend.api.dto;

import java.math.BigDecimal;

public record PriceRangeDto(BigDecimal min, BigDecimal max) {
}
