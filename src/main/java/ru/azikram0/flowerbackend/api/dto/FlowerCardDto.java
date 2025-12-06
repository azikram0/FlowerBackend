package ru.azikram0.flowerbackend.api.dto;

import java.math.BigDecimal;

public record FlowerCardDto(int id, int colorId, String name, String photoUrl, BigDecimal price) {
}
