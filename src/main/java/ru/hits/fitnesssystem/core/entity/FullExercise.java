package ru.hits.fitnesssystem.core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "full_exercises")
public class FullExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "full_exercise_exercise",
            joinColumns = @JoinColumn(name = "full_exercise_id"),
            inverseJoinColumns = @JoinColumn(name = "exercise_id")
    )
    @Builder.Default
    private List<Exercise> exercises = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "full_exercise_approach",
            joinColumns = @JoinColumn(name = "full_exercise_id"),
            inverseJoinColumns = @JoinColumn(name = "approach_id")
    )
    @Builder.Default
    private List<Approach> approaches = new ArrayList<>();

    @Column(name = "description", nullable = false)
    private String description;
}