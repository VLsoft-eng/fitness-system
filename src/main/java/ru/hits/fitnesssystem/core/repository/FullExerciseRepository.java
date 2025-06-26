package ru.hits.fitnesssystem.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hits.fitnesssystem.core.entity.FullExercise;

public interface FullExerciseRepository extends JpaRepository<FullExercise, Long> {
    boolean existsByExerciseId(Long exerciseId);
    boolean existsByApproachId(Long approachId);
}