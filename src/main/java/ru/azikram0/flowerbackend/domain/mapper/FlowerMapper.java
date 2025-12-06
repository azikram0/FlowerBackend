package ru.azikram0.flowerbackend.domain.mapper;

import org.springframework.stereotype.Component;
import ru.azikram0.flowerbackend.api.dto.FlowerCardDto;
import ru.azikram0.flowerbackend.api.dto.FlowerDto;
import ru.azikram0.flowerbackend.store.entity.db.CareTag;
import ru.azikram0.flowerbackend.store.entity.db.Flower;
import ru.azikram0.flowerbackend.store.entity.db.FlowerColor;

import java.util.List;

@Component
public class FlowerMapper {
    public FlowerCardDto mapToCard(Flower flower, FlowerColor flowerColor) {
        return new FlowerCardDto(
                flower.getId(),
                flowerColor.getColor().getId(),
                flower.getName(),
                flowerColor.getPhotoUrl(),
                flower.getPrice()
        );
    }

    public FlowerDto map(Flower flower, FlowerColor flowerColor) {
        List<String> colorNames = flower.getFlowerColors().stream()
                .map(fc -> fc.getColor().getName())
                .toList();
        List<String> careTags = flower.getCareTags().stream().map(CareTag::getName).toList();
        return new FlowerDto(
                flower.getId(),
                flower.getName(),
                flowerColor.getColor().getId(),
                flowerColor.getPhotoUrl(),
                flower.getDescription(),
                colorNames,
                careTags,
                flower.getFamily().getName(),
                flower.getPrice()
        );
    }
}
