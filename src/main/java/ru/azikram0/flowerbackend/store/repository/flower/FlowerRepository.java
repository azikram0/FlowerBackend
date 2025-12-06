package ru.azikram0.flowerbackend.store.repository.flower;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.azikram0.flowerbackend.store.entity.PriceRange;
import ru.azikram0.flowerbackend.store.entity.db.Flower;

public interface FlowerRepository extends JpaRepository<Flower, Integer>, JpaSpecificationExecutor<Flower> {
    @Query("""
            SELECT new ru.azikram0.flowerbackend.store.entity.PriceRange(
                    MIN(f.price),
                    MAX(f.price)
                )
                FROM Flower f
            """)
    PriceRange findMinAndMaxPrice();
}
