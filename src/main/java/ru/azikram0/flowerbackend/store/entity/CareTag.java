package ru.azikram0.flowerbackend.store.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "care_tag")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class CareTag {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}
    )
    @JoinTable(
            name = "flower_care_tag",
            joinColumns = @JoinColumn(name = "care_tag_id"),
            inverseJoinColumns = @JoinColumn(name = "flower_id")
    )
    private List<Flower> flowers;
}