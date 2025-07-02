package ru.hits.fitnesssystem.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import lombok.*;

@Entity
@Table(name = "train_machine")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainMachine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "base64_image", columnDefinition = "TEXT")
    private String base64Image;

    @Column(name = "count", nullable = false)
    private Long count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_room_id", nullable = false)
    private GymRoom gymRoom;

    @Override
    public String toString() {
        return "TrainMachine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", count=" + count +
                '}';
    }
}