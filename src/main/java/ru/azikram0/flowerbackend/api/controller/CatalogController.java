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
    @GetMapping("/flower/{id}/{colorId}")
    public FlowerDto getFlowerByIdAndColorId(@PathVariable int id, @PathVariable int colorId) {
        return null;
    }

    @GetMapping("/search-flowers")
    public List<FlowerCardDto> searchFlowers() {
        return null;
    }

    @GetMapping("/all-families")
    public List<String> getAllFamilies() {
        // TODO("Вернуть список всех семейств")
        return null;
    }

    @GetMapping("/all-colors")
    public List<String> getAllColors() {
        return null;
    }

    @GetMapping("/all-care-tags")
    public List<String> getAllCareTags() {
        return null;
    }

    @GetMapping("/price-range")
    public PriceRangeDto getPriceRange() {
        return null;
    }
}
