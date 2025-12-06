package ru.azikram0.flowerbackend.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record FlowerDto(
        int id,
        String name,
        String photoUrl,
        String description,
        List<String> colorNames,
        List<String> careTags,
        BigDecimal price
) {
}
