package ru.azikram0.flowerbackend.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.azikram0.flowerbackend.store.entity.Flower;

public interface FlowerRepository extends JpaRepository<Flower, Integer> {
}
