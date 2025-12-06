package ru.azikram0.flowerbackend.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.azikram0.flowerbackend.store.entity.db.Color;

public interface ColorRepository extends JpaRepository<Color, Integer> {
}
