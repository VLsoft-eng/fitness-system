package ru.hits.fitnesssystem.core.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approach_id", nullable = false)
    private Approach approach;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private User trainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_room_id")
    private GymRoom gymRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_machine_id")
    private TrainMachine trainingMachine;
}