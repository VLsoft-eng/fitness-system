package ru.hits.fitnesssystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.fitnesssystem.core.entity.*;
import ru.hits.fitnesssystem.core.exception.BadRequestException;
import ru.hits.fitnesssystem.core.exception.NotFoundException;
import ru.hits.fitnesssystem.core.repository.*;
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
    private final TrainMachineRepository trainMachineRepository;

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

    @Transactional
    public FullExerciseDto createFullExercise(FullExerciseCreateDto fullExerciseCreateDto) {
        String trainerUsername = SecurityUtils.getCurrentUsername();
        User user = userRepository.findUserByUsername(trainerUsername).orElseThrow(() -> new NotFoundException("User not found"));
        Exercise exercise = exerciseRepository.findById(fullExerciseCreateDto.exerciseId())
                .orElseThrow(() -> new NotFoundException("Exercise not found"));
        Approach approach = approachRepository.findById(fullExerciseCreateDto.approachId())
                .orElseThrow(() -> new NotFoundException("Approach not found"));
        TrainMachine trainMachine = trainMachineRepository.findById(fullExerciseCreateDto.trainMachineId())
                .orElseThrow(() -> new NotFoundException("Train machine not found"));


        // Проверка, что exercise и approach принадлежат текущему тренеру
        if (!exercise.getTrainer().getId().equals(user.getId()) || !approach.getTrainer().getId().equals(user.getId())) {
            throw new NotFoundException("Exercise or Approach does not belong to the current trainer");
        }

        FullExercise fullExercise = FullExercise.builder()
                .exercise(exercise)
                .approach(approach)
                .trainer(user)
                .trainingMachine(trainMachine)
                .build();

        fullExerciseRepository.save(fullExercise);

        return new FullExerciseDto(
                fullExercise.getId(),
                new ExerciseDto(exercise.getId(), exercise.getTitle(), exercise.getDescription()),
                new ApproachDto(approach.getId(), approach.getApproachesCount(), approach.getRepetitionPerApproachCount()),
                new TrainMachineDto(trainMachine.getId(), trainMachine.getName(), trainMachine.getDescription(), trainMachine.getBase64Image(), trainMachine.getCount(), trainMachine.getGymRoom().getId())
        );
    }

    public FullExerciseDto getFullExerciseById(Long id) {
        FullExercise fullExercise = fullExerciseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Full exercise not found"));
        return new FullExerciseDto(
                fullExercise.getId(),
                new ExerciseDto(
                        fullExercise.getExercise().getId(),
                        fullExercise.getExercise().getTitle(),
                        fullExercise.getExercise().getDescription()
                ),
                new ApproachDto(
                        fullExercise.getApproach().getId(),
                        fullExercise.getApproach().getApproachesCount(),
                        fullExercise.getApproach().getRepetitionPerApproachCount()
                ),
                new TrainMachineDto(
                        fullExercise.getTrainingMachine().getId(),
                        fullExercise.getTrainingMachine().getName(),
                        fullExercise.getTrainingMachine().getDescription(),
                        fullExercise.getTrainingMachine().getBase64Image(),
                        fullExercise.getTrainingMachine().getCount(),
                        fullExercise.getTrainingMachine().getGymRoom().getId()
                )
        );
    }

    @Transactional
    public void deleteApproach(Long id) {
        String trainerUsername = SecurityUtils.getCurrentUsername();
        User user = userRepository.findUserByUsername(trainerUsername).orElseThrow(() -> new NotFoundException("User not found"));
        Approach approach = approachRepository.findById(id).orElseThrow(() -> new NotFoundException("Approach not found"));
        if (!approach.getTrainer().getId().equals(user.getId())) {
            throw new NotFoundException("Approach does not belong to the current trainer");
        }
        if (fullExerciseRepository.existsByApproachId(id)) {
            throw new BadRequestException("Cannot delete approach because it is used in a FullExercise");
        }
        approachRepository.deleteById(id);
    }

    @Transactional
    public void deleteExercise(Long id) {
        String trainerUsername = SecurityUtils.getCurrentUsername();
        User user = userRepository.findUserByUsername(trainerUsername).orElseThrow(() -> new NotFoundException("User not found"));
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(() -> new NotFoundException("Exercise not found"));
        if (!exercise.getTrainer().getId().equals(user.getId())) {
            throw new NotFoundException("Exercise does not belong to the current trainer");
        }
        if (fullExerciseRepository.existsByExerciseId(id)) {
            throw new BadRequestException("Cannot delete exercise because it is used in a FullExercise");
        }
        exerciseRepository.deleteById(id);
    }

    @Transactional
    public void deleteFullExercise(Long id) {
        String trainerUsername = SecurityUtils.getCurrentUsername();
        User user = userRepository.findUserByUsername(trainerUsername).orElseThrow(() -> new NotFoundException("User not found"));
        FullExercise fullExercise = fullExerciseRepository.findById(id).orElseThrow(() -> new NotFoundException("Full exercise not found"));
        if (!fullExercise.getTrainer().getId().equals(user.getId())) {
            throw new NotFoundException("Full exercise does not belong to the current trainer");
        }
        fullExerciseRepository.deleteById(id);
    }

    public FullExercisesListDto getAllFullExercisesForCurrentTrainer() {
        String trainerUsername = SecurityUtils.getCurrentUsername();
        User user = userRepository.findUserByUsername(trainerUsername)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<FullExercise> fullExercises = fullExerciseRepository.findAllByTrainerId(user.getId());
        List<FullExerciseDto> fullExerciseDtos = fullExercises.stream()
                .map(fullExercise -> new FullExerciseDto(
                        fullExercise.getId(),
                        new ExerciseDto(
                                fullExercise.getExercise().getId(),
                                fullExercise.getExercise().getTitle(),
                                fullExercise.getExercise().getDescription()
                        ),
                        new ApproachDto(
                                fullExercise.getApproach().getId(),
                                fullExercise.getApproach().getApproachesCount(),
                                fullExercise.getApproach().getRepetitionPerApproachCount()
                        ),
                        new TrainMachineDto(
                                fullExercise.getTrainingMachine().getId(),
                                fullExercise.getTrainingMachine().getName(),
                                fullExercise.getTrainingMachine().getDescription(),
                                fullExercise.getTrainingMachine().getBase64Image(),
                                fullExercise.getTrainingMachine().getCount(),
                                fullExercise.getTrainingMachine().getGymRoom().getId()
                        )
                ))
                .toList();
        return new FullExercisesListDto(fullExerciseDtos);
    }
}