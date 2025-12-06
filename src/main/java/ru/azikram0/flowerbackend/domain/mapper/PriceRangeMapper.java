package ru.azikram0.flowerbackend.domain.mapper;

import org.springframework.stereotype.Component;
import ru.azikram0.flowerbackend.api.dto.PriceRangeDto;
import ru.azikram0.flowerbackend.store.entity.PriceRange;

@Component
public class PriceRangeMapper {
    public PriceRangeDto map(PriceRange priceRange) {
        return new PriceRangeDto(priceRange.min(), priceRange.max());
    }
}
