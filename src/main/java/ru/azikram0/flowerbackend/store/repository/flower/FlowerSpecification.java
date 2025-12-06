package ru.azikram0.flowerbackend.store.repository.flower;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.azikram0.flowerbackend.store.entity.db.CareTag;
import ru.azikram0.flowerbackend.store.entity.db.Color;
import ru.azikram0.flowerbackend.store.entity.db.Flower;
import ru.azikram0.flowerbackend.store.entity.db.FlowerColor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FlowerSpecification {
    private static final String NAME = "name";
    private static final String PRICE = "price";
    private static final String FAMILY = "family";
    private static final String CARE_TAGS = "careTags";
    private static final String FLOWER_COLORS = "flowerColors";
    private static final String COLOR = "color";

    public static Specification<Flower> build(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            List<String> familyNames,
            List<String> careTagNames,
            List<String> colorNames
    ) {
        return (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.upper(root.get(NAME)), "%" + name.toUpperCase() + "%"));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(PRICE), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(PRICE), maxPrice));
            }
            if (familyNames != null && !familyNames.isEmpty()) {
                predicates.add(root.get(FAMILY).get(NAME).in(familyNames));
            }
            if (careTagNames != null && !careTagNames.isEmpty()) {
                Join<Flower, CareTag> join = root.join(CARE_TAGS, JoinType.LEFT);
                predicates.add(join.get(NAME).in(careTagNames));
            }
            if (colorNames != null && !colorNames.isEmpty()) {
                Join<Flower, FlowerColor> fcJoin = root.join(FLOWER_COLORS, JoinType.LEFT);
                Join<FlowerColor, Color> colorJoin = fcJoin.join(COLOR, JoinType.LEFT);
                predicates.add(colorJoin.get(NAME).in(colorNames));
            }
            return cb.and(predicates);
        };
    }
}
