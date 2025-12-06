package ru.azikram0.flowerbackend.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.azikram0.flowerbackend.api.dto.FlowerCardDto;
import ru.azikram0.flowerbackend.api.dto.FlowerDto;
import ru.azikram0.flowerbackend.api.dto.PriceRangeDto;

import java.util.List;

@RestController
@RequestMapping("/catalog")
public class CatalogController {
    @GetMapping("/flower/{id}")
    public FlowerDto getFlowerById(@PathVariable int id) {
        return null;
    }

    @GetMapping("/search-flowers")
    List<FlowerCardDto> searchFlowers() {
        return null;
    }

    @GetMapping("/all-families")
    List<String> getAllFamilies() {
        // TODO("Вернуть список всех семейств")
        return null;
    }

    @GetMapping("/all-colors")
    List<String> getAllColors() {
        return null;
    }

    @GetMapping("/all-care-tags")
    List<String> getAllCareTags() {
        return null;
    }

    @GetMapping("/price-range")
    PriceRangeDto getPriceRange() {
        return null;
    }
}
