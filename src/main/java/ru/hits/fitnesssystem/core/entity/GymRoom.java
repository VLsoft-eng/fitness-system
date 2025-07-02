package ru.hits.fitnesssystem.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import lombok.*;
import java.util.ArrayList;

@Entity
@Table(name = "gym_rooms")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GymRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "capacity", nullable = false)
    private Long capacity;

    @Column(name = "base64_image", columnDefinition = "TEXT")
    private String base64Image;

    @OneToMany(mappedBy = "gymRoom", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TrainMachine> trainMachines = new ArrayList<>();
}