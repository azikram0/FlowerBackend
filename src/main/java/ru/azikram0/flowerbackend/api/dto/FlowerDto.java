package ru.azikram0.flowerbackend.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record FlowerDto(
        int id,
        String name,
        int colorId,
        String photoUrl,
        String description,
        List<String> colorNames,
        List<String> careTags,
        List<String> photoUrls,
        String familyName,
        BigDecimal price
) {
}
