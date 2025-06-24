package ru.hits.fitnesssystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.fitnesssystem.core.repository.ApproachRepository;
import ru.hits.fitnesssystem.core.repository.ExerciseRepository;
import ru.hits.fitnesssystem.core.repository.FullExerciseRepository;

@RequiredArgsConstructor
@Service
public class TrainingService {
    private final ExerciseRepository exerciseRepository;
    private final ApproachRepository approachRepository;
    private final FullExerciseRepository fullExerciseRepository;


}
