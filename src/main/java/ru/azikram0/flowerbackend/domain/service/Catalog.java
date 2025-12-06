package ru.azikram0.flowerbackend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.azikram0.flowerbackend.api.dto.FlowerCardDto;
import ru.azikram0.flowerbackend.api.dto.FlowerDto;
import ru.azikram0.flowerbackend.api.dto.PriceRangeDto;
import ru.azikram0.flowerbackend.domain.mapper.FlowerMapper;
import ru.azikram0.flowerbackend.domain.mapper.PriceRangeMapper;
import ru.azikram0.flowerbackend.store.entity.db.CareTag;
import ru.azikram0.flowerbackend.store.entity.db.Color;
import ru.azikram0.flowerbackend.store.entity.db.Family;
import ru.azikram0.flowerbackend.store.entity.db.Flower;
import ru.azikram0.flowerbackend.store.repository.CareTagRepository;
import ru.azikram0.flowerbackend.store.repository.ColorRepository;
import ru.azikram0.flowerbackend.store.repository.FamilyRepository;
import ru.azikram0.flowerbackend.store.repository.flower.FlowerRepository;
import ru.azikram0.flowerbackend.store.repository.flower.FlowerSpecification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class Catalog {
    private final FlowerRepository flowerRepository;
    private final CareTagRepository careTagRepository;
    private final ColorRepository colorRepository;
    private final FamilyRepository familyRepository;

    private final FlowerMapper flowerMapper;
    private final PriceRangeMapper priceRangeMapper;

    @Autowired
    public Catalog(
            FlowerRepository flowerRepository,
            CareTagRepository careTagRepository,
            ColorRepository colorRepository,
            FamilyRepository familyRepository,
            FlowerMapper flowerMapper,
            PriceRangeMapper priceRangeMapper
    ) {
        this.flowerRepository = flowerRepository;
        this.careTagRepository = careTagRepository;
        this.colorRepository = colorRepository;
        this.familyRepository = familyRepository;
        this.flowerMapper = flowerMapper;
        this.priceRangeMapper = priceRangeMapper;
    }

    public List<String> getAllFamilies() {
        return familyRepository.findAll().stream().map(Family::getName).toList();
    }

    public List<String> getAllColors() {
        return colorRepository.findAll().stream().map(Color::getName).toList();
    }

    public List<String> getAllCareTags() {
        return careTagRepository.findAll().stream().map(CareTag::getName).toList();
    }

    public PriceRangeDto getPriceRange() {
        return priceRangeMapper.map(flowerRepository.findMinAndMaxPrice());
    }

    public FlowerDto getFlowerByIdAndColorId(int id, int colorId) {
        Optional<Flower> optionalFlower = flowerRepository.findById(id);
        if (optionalFlower.isEmpty()) {
            return null;
        }
        Flower flower = optionalFlower.get();
        return flower.getFlowerColors().stream()
                .filter(flowerColor -> flowerColor.getColor().getId() == colorId)
                .findFirst()
                .map(flowerColor -> flowerMapper.map(flower, flowerColor))
                .orElse(null);
    }

    public List<FlowerCardDto> searchFlowers(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<String> familyNames,
            List<String> careTagNames,
            List<String> colorNames
    ) {
        List<Flower> flowers = flowerRepository.findAll(
                FlowerSpecification.build(name, minPrice, maxPrice, familyNames, careTagNames, colorNames)
        );
        return flowers.stream()
                .flatMap(flower -> flower.getFlowerColors()
                        .stream()
                        .map(flowerColor -> flowerMapper.mapToCard(flower, flowerColor)))
                .toList();
    }
}
