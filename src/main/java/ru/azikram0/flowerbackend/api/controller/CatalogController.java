package ru.azikram0.flowerbackend.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.azikram0.flowerbackend.api.dto.FlowerCardDto;
import ru.azikram0.flowerbackend.api.dto.FlowerDto;
import ru.azikram0.flowerbackend.api.dto.PriceRangeDto;
import ru.azikram0.flowerbackend.domain.service.Catalog;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/catalog")
public class CatalogController {
    private final Catalog catalog;

    @Autowired
    public CatalogController(Catalog catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/flower/{id}/{colorId}")
    public FlowerDto getFlowerByIdAndColorId(@PathVariable int id, @PathVariable int colorId) {
        FlowerDto flowerDto = catalog.getFlowerByIdAndColorId(id, colorId);
        if (flowerDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return flowerDto;
    }

    @GetMapping("/search-flowers")
    public List<FlowerCardDto> searchFlowers(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(name = "familyNames", required = false) List<String> familyNames,
            @RequestParam(name = "careTagNames", required = false) List<String> careTagNames,
            @RequestParam(name = "colorNames", required = false) List<String> colorNames
    ) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return catalog.searchFlowers(name, minPrice, maxPrice, familyNames, careTagNames, colorNames);
    }

    @GetMapping("/all-families")
    public List<String> getAllFamilies() {
        return catalog.getAllFamilies();
    }

    @GetMapping("/all-colors")
    public List<String> getAllColors() {
        return catalog.getAllColors();
    }

    @GetMapping("/all-care-tags")
    public List<String> getAllCareTags() {
        return catalog.getAllCareTags();
    }

    @GetMapping("/price-range")
    public PriceRangeDto getPriceRange() {
        return catalog.getPriceRange();
    }
}
