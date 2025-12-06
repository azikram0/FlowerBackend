package ru.azikram0.flowerbackend.store.entity.db;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "flower_color")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class FlowerColor {
    @EmbeddedId
    private FlowerColorId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("flowerId")
    @JoinColumn(name = "flower_id", nullable = false)
    private Flower flower;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("colorId")
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @Column(name = "photo_url", nullable = false)
    private String photoUrl;

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class FlowerColorId implements Serializable {
        @Column(name = "flower_id", nullable = false)
        private int flowerId;

        @Column(name = "color_id", nullable = false)
        private int colorId;

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            FlowerColorId that = (FlowerColorId) obj;
            return flowerId == that.flowerId && colorId == that.colorId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(flowerId, colorId);
        }
    }
}
