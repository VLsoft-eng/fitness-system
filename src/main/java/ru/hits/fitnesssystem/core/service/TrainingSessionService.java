package ru.hits.fitnesssystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.fitnesssystem.core.entity.FullExercise;
import ru.hits.fitnesssystem.core.entity.TrainingSession;
import ru.hits.fitnesssystem.core.entity.User;
import ru.hits.fitnesssystem.core.enumeration.EnrollmentStatus;
import ru.hits.fitnesssystem.core.enumeration.TrainingSessionType;
import ru.hits.fitnesssystem.core.enumeration.UserRole;
import ru.hits.fitnesssystem.core.exception.BadRequestException;
import ru.hits.fitnesssystem.core.exception.NotFoundException;
import ru.hits.fitnesssystem.core.repository.FullExerciseRepository;
import ru.hits.fitnesssystem.core.repository.TrainingSessionRepository;
import ru.hits.fitnesssystem.core.repository.UserRepository;
import ru.hits.fitnesssystem.core.security.SecurityUtils;
import ru.hits.fitnesssystem.rest.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final FullExerciseRepository fullExerciseRepository;
    private final UserRepository userRepository;

    @Transactional
    public TrainingSessionDto createTrainingSession(CreateTrainingSessionDto dto) {
        User trainer = null;
        if (dto.trainerId() != null) {
            trainer = userRepository.findById(dto.trainerId())
                    .orElseThrow(() -> new NotFoundException("Тренер с ID " + dto.trainerId() + " не найден."));

            if (trainer.getRole() != UserRole.TRAINER) {
                throw new BadRequestException("Пользователь с ID " + dto.trainerId() + " не является тренером.");
            }
        } else if (dto.type() == TrainingSessionType.PERSONAL) {
            throw new BadRequestException("Для персональной тренировки должен быть указан тренер.");
        }

        LocalDateTime endTime = dto.startTime().plusMinutes(dto.durationMinutes());

        if (trainer != null && trainingSessionRepository.findAllByTrainerAndStartTimeBetween(trainer, dto.startTime(), endTime, PageRequest.of(0, 1)).hasContent()) {
            throw new BadRequestException("У тренера уже есть занятие в указанное время.");
        }

        Integer maxParticipants = dto.maxParticipants();
        if (dto.type() == TrainingSessionType.GROUP) {
            if (maxParticipants == null || maxParticipants <= 0) {
                throw new BadRequestException("Для групповой тренировки должно быть указано максимальное количество участников.");
            }
        } else {
            if (maxParticipants != null && maxParticipants != 1) {
                throw new BadRequestException("Для персональной тренировки максимальное количество участников должно быть 1 или не указано.");
            }
            maxParticipants = 1;
        }

        TrainingSession session = TrainingSession.builder()
                .name(dto.name())
                .description(dto.description())
                .type(dto.type())
                .trainer(trainer)
                .startTime(dto.startTime())
                .endTime(endTime)
                .durationMinutes(dto.durationMinutes())
                .maxParticipants(maxParticipants)
                .currentParticipants(0)
                .location(dto.location())
                .build();

        session = trainingSessionRepository.save(session);
        return TrainingSessionDto.fromEntity(session);
    }

    @Transactional
    public TrainingSessionDto updateTrainingSession(Long id, UpdateTrainingSessionDto dto) {
        TrainingSession existingSession = trainingSessionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Занятие с ID " + id + " не найдено."));

        String currentUsername = SecurityUtils.getCurrentUsername();
        User currentUser = userRepository.findUserByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Авторизованный пользователь не найден."));
        if (currentUser.getRole() == UserRole.TRAINER) {
            if (existingSession.getTrainer() == null || !existingSession.getTrainer().getId().equals(currentUser.getId())) {
                throw new BadRequestException("Тренер может обновлять только свои собственные занятия.");
            }
            if (dto.trainerId() != null && !dto.trainerId().equals(currentUser.getId())) {
                throw new BadRequestException("Тренер не может менять тренера на занятии, если это не он сам.");
            }
        }

        User newTrainer = existingSession.getTrainer();
        if (dto.trainerId() != null && (existingSession.getTrainer() == null || !dto.trainerId().equals(existingSession.getTrainer().getId()))) {
            newTrainer = userRepository.findById(dto.trainerId())
                    .orElseThrow(() -> new NotFoundException("Тренер с ID " + dto.trainerId() + " не найден."));

            if (newTrainer.getRole() != UserRole.TRAINER) {
                throw new BadRequestException("Пользователь с ID " + dto.trainerId() + " не является тренером.");
            }
            existingSession.setTrainer(newTrainer);
        } else if (dto.trainerId() == null && existingSession.getType() == TrainingSessionType.PERSONAL) {
            throw new BadRequestException("Для персональной тренировки тренер не может быть удален.");
        }

        if (dto.type() != null && dto.type() != existingSession.getType()) {
            if (dto.type() == TrainingSessionType.PERSONAL) {
                if (existingSession.getTrainer() == null) {
                    throw new BadRequestException("Невозможно сменить на персональное занятие без тренера.");
                }
                if (existingSession.getCurrentParticipants() > 1) {
                    throw new BadRequestException("Невозможно сменить на персональное занятие, так как количество записавшихся больше 1.");
                }
                existingSession.setMaxParticipants(1);
            } else if (dto.type() == TrainingSessionType.GROUP) {
                if (dto.maxParticipants() == null || dto.maxParticipants() <= 0) {
                    throw new BadRequestException("При смене на групповую тренировку, необходимо указать максимальное количество участников.");
                }
                existingSession.setMaxParticipants(dto.maxParticipants());
            }
            existingSession.setType(dto.type());
        }

        LocalDateTime newStartTime = dto.startTime() != null ? dto.startTime() : existingSession.getStartTime();
        Integer newDurationMinutes = dto.durationMinutes() != null ? dto.durationMinutes() : existingSession.getDurationMinutes();
        LocalDateTime newEndTime = newStartTime.plusMinutes(newDurationMinutes);

        if (existingSession.getTrainer() != null && (dto.startTime() != null || dto.durationMinutes() != null || (dto.trainerId() != null && !dto.trainerId().equals(existingSession.getTrainer().getId())))) {
            List<TrainingSession> conflictingSessions = trainingSessionRepository
                    .findAllByTrainerAndStartTimeBetween(existingSession.getTrainer(), newStartTime.minusMinutes(1), newEndTime.plusMinutes(1), Pageable.unpaged())
                    .getContent();

            boolean conflictWithOtherSession = conflictingSessions.stream()
                    .anyMatch(s -> !s.getId().equals(existingSession.getId()) &&
                            s.getStartTime().isBefore(newEndTime) &&
                            s.getEndTime().isAfter(newStartTime));

            if (conflictWithOtherSession) {
                throw new BadRequestException("У тренера уже есть другое занятие в указанное время.");
            }
        }

        existingSession.setStartTime(newStartTime);
        existingSession.setDurationMinutes(newDurationMinutes);
        existingSession.setEndTime(newEndTime);

        if (dto.maxParticipants() != null && existingSession.getType() == TrainingSessionType.GROUP) {
            if (dto.maxParticipants() <= 0) {
                throw new BadRequestException("Для групповой тренировки максимальное количество участников должно быть положительным.");
            }
            if (existingSession.getCurrentParticipants() > dto.maxParticipants()) {
                throw new BadRequestException("Невозможно установить максимальное количество участников меньше текущего числа записавшихся.");
            }
            existingSession.setMaxParticipants(dto.maxParticipants());
        }

        if (dto.name() != null) existingSession.setName(dto.name());
        if (dto.description() != null) existingSession.setDescription(dto.description());
        if (dto.location() != null) existingSession.setLocation(dto.location());

        TrainingSession updatedSession = trainingSessionRepository.save(existingSession);
        return TrainingSessionDto.fromEntity(updatedSession);
    }

    @Transactional
    public void deleteTrainingSession(Long id) {
        TrainingSession session = trainingSessionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Занятие с ID " + id + " не найдено."));

        if (session.getCurrentParticipants() > 0) {
            throw new BadRequestException("Невозможно удалить занятие, так как на него есть записавшиеся пользователи. Сначала отмените все записи.");
        }

        if (trainingSessionRepository.existsByIdAndEnrollmentsStatus(id, EnrollmentStatus.WAITLIST)) {
            throw new BadRequestException("Невозможно удалить занятие, так как на него есть пользователи в листе ожидания. Сначала отмените все записи.");
        }
        trainingSessionRepository.delete(session);
    }

    @Transactional(readOnly = true)
    public TrainingSessionDto getTrainingSession(Long id) {
        TrainingSession session = trainingSessionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Занятие с ID " + id + " не найдено."));
        return TrainingSessionDto.fromEntity(session);
    }

    @Transactional(readOnly = true)
    public TrainingSessionListDto getAllTrainingSessions(Pageable pageable, LocalDateTime startDate, LocalDateTime endDate, Long trainerId) {
        Page<TrainingSession> sessionPage;
        User trainer = null;

        if (trainerId != null) {
            trainer = userRepository.findById(trainerId)
                    .orElseThrow(() -> new NotFoundException("Тренер с ID " + trainerId + " не найден."));
            if (trainer.getRole() != UserRole.TRAINER) {
                throw new BadRequestException("Пользователь с ID " + trainerId + " не является тренером.");
            }
        }

        if (startDate != null && endDate != null && trainer != null) {
            sessionPage = trainingSessionRepository.findAllByTrainerAndStartTimeBetween(trainer, startDate, endDate, pageable);
        } else if (startDate != null && endDate != null) {
            sessionPage = trainingSessionRepository.findAllByStartTimeBetween(startDate, endDate, pageable);
        } else if (trainer != null) {
            sessionPage = trainingSessionRepository.findAllByTrainer(trainer, pageable);
        } else {
            sessionPage = trainingSessionRepository.findAll(pageable);
        }

        List<TrainingSessionDto> sessionDtos = sessionPage.getContent().stream()
                .map(TrainingSessionDto::fromEntity)
                .collect(Collectors.toList());

        return new TrainingSessionListDto(
                sessionDtos,
                sessionPage.getTotalElements(),
                sessionPage.getTotalPages(),
                sessionPage.getNumber(),
                sessionPage.isLast()
        );
    }

    @Transactional
    public void attachFullExercise(AttachFullExerciseDto attachFullExerciseDto, Long trainingSessionId) {
        TrainingSession trainingSession = trainingSessionRepository.findById(trainingSessionId)
                .orElseThrow(() -> new NotFoundException("Тренировка не найдена"));
        List<FullExercise> fullExercises = attachFullExerciseDto.exerciseIds().stream()
                .map(id -> fullExerciseRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Упражнение с ID " + id + " не найдено")))
                .toList();

        trainingSession.getFullExercises().addAll(fullExercises);
        trainingSessionRepository.save(trainingSession);
    }
    @Transactional
    public void detachFullExercise(Long trainingSessionId, Long fullExerciseId) {
        TrainingSession trainingSession = trainingSessionRepository.findById(trainingSessionId)
                .orElseThrow(() -> new NotFoundException("Тренировка с ID " + trainingSessionId + " не найдена"));
        FullExercise fullExercise = fullExerciseRepository.findById(fullExerciseId)
                .orElseThrow(() -> new NotFoundException("Упражнение с ID " + fullExerciseId + " не найдено"));

        if (!trainingSession.getFullExercises().remove(fullExercise)) {
            throw new BadRequestException("Упражнение с ID " + fullExerciseId + " не связано с тренировкой с ID " + trainingSessionId);
        }

        trainingSessionRepository.save(trainingSession);
    }
}