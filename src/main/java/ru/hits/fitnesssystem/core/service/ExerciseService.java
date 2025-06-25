package ru.hits.fitnesssystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.fitnesssystem.core.entity.Approach;
import ru.hits.fitnesssystem.core.entity.Exercise;
import ru.hits.fitnesssystem.core.entity.FullExercise;
import ru.hits.fitnesssystem.core.entity.User;
import ru.hits.fitnesssystem.core.exception.NotFoundException;
import ru.hits.fitnesssystem.core.repository.ApproachRepository;
import ru.hits.fitnesssystem.core.repository.ExerciseRepository;
import ru.hits.fitnesssystem.core.repository.FullExerciseRepository;
import ru.hits.fitnesssystem.core.repository.UserRepository;
import ru.hits.fitnesssystem.core.security.SecurityUtils;
import ru.hits.fitnesssystem.rest.model.*;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final FullExerciseRepository fullExerciseRepository;
    private final ApproachRepository approachRepository;
    private final UserRepository userRepository;

    public ApproachDto createApproach(ApproachCreateDto approachCreateDto) {
        String trainerUsername = SecurityUtils.getCurrentUsername();
        User user = userRepository.findUserByUsername(trainerUsername).orElseThrow(() -> new NotFoundException("User not found"));
        Approach approach = Approach.builder()
                .trainer(user)
                .approachesCount(approachCreateDto.approachesCount())
                .repetitionPerApproachCount(approachCreateDto.repetitionPerApproachCount())
                .build();
        approachRepository.save(approach);

        return new ApproachDto(
                approach.getId(),
                approach.getApproachesCount(),
                approach.getRepetitionPerApproachCount()
        );
    }

    public ApproachesListDto getApproachesForCurrentTrainer() {
        String trainerUsername = SecurityUtils.getCurrentUsername();
        User user = userRepository.findUserByUsername(trainerUsername).orElseThrow(() -> new NotFoundException("User not found"));

        List<Approach> approaches = approachRepository.findAllByTrainerId(user.getId());
        List<ApproachDto> approachDtos = approaches.stream()
                .map(approach -> new ApproachDto(approach.getId(), approach.getApproachesCount(), approach.getRepetitionPerApproachCount()))
                .toList();
        return new ApproachesListDto(approachDtos);
    }

    public ExerciseListDto getExercisesForCurrentTrainer() {
        String trainerUsername = SecurityUtils.getCurrentUsername();
        User user = userRepository.findUserByUsername(trainerUsername).orElseThrow(() -> new NotFoundException("User not found"));

        List<Exercise> exercises = exerciseRepository.findAllByTrainerId(user.getId());
        List<ExerciseDto> exerciseDtos = exercises.stream()
                .map(exercise -> new ExerciseDto(exercise.getId(), exercise.getTitle(), exercise.getDescription()))
                .toList();
        return new ExerciseListDto(exerciseDtos);
    }

    public ExerciseDto createExercise(ExerciseCreateDto exerciseCreateDto) {
        String trainerUsername = SecurityUtils.getCurrentUsername();
        User user = userRepository.findUserByUsername(trainerUsername).orElseThrow(() -> new NotFoundException("User not found"));
        Exercise exercise = Exercise.builder()
                .trainer(user)
                .title(exerciseCreateDto.title())
                .description(exerciseCreateDto.description())
                .build();
        exerciseRepository.save(exercise);

        return new ExerciseDto(
                exercise.getId(),
                exercise.getTitle(),
                exercise.getDescription()
        );
    }

    public FullExerciseDto createFullExercise(FullExerciseCreateDto fullExerciseCreateDto) {
        String trainerUsername = SecurityUtils.getCurrentUsername();
        User user = userRepository.findUserByUsername(trainerUsername).orElseThrow(() -> new NotFoundException("User not found"));
        Exercise exercise = exerciseRepository.findById(fullExerciseCreateDto.exerciseId()).orElseThrow(() -> new NotFoundException("Exercise not found"));
        Approach approach = approachRepository.findById(fullExerciseCreateDto.approachId()).orElseThrow(() -> new NotFoundException("Approach not found"));
        FullExercise fullExercise = FullExercise.builder()
                .exercise(exercise)
                .approach(approach)
                .build();

        fullExerciseRepository.save(fullExercise);

        return new FullExerciseDto(
                fullExercise.getId(),
                new ExerciseDto(exercise.getId(), exercise.getTitle(), exercise.getDescription()),
                new ApproachDto(approach.getId(), approach.getApproachesCount(), approach.getRepetitionPerApproachCount())
        );
    }

    public FullExerciseDto getFullExerciseById(Long id) {
        FullExercise fullExercise = fullExerciseRepository.findById(id).orElseThrow(() -> new NotFoundException("Full exercise not found"));
        return new FullExerciseDto(
                fullExercise.getId(),
                new ExerciseDto(fullExercise.getExercise().getId(), fullExercise.getExercise().getTitle(), fullExercise.getExercise().getDescription()),
                new ApproachDto(fullExercise.getApproach().getId(), fullExercise.getApproach().getApproachesCount(), fullExercise.getApproach().getRepetitionPerApproachCount())
        );
    }
}
