package ru.hits.fitnesssystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.fitnesssystem.core.entity.Enrollment;
import ru.hits.fitnesssystem.core.entity.TrainingSession;
import ru.hits.fitnesssystem.core.entity.User;
import ru.hits.fitnesssystem.core.enumeration.EnrollmentCallType;
import ru.hits.fitnesssystem.core.enumeration.EnrollmentStatus;
import ru.hits.fitnesssystem.core.enumeration.UserRole;
import ru.hits.fitnesssystem.core.exception.BadRequestException;
import ru.hits.fitnesssystem.core.exception.NotFoundException;
import ru.hits.fitnesssystem.core.repository.EnrollmentRepository;
import ru.hits.fitnesssystem.core.repository.TrainingSessionRepository;
import ru.hits.fitnesssystem.core.repository.UserRepository;
import ru.hits.fitnesssystem.core.security.SecurityUtils;
import ru.hits.fitnesssystem.rest.model.EnrollmentDto;
import ru.hits.fitnesssystem.rest.model.EnrollmentListDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public EnrollmentDto enrollUserToSession(Long sessionId) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (currentUsername == null) {
            throw new BadRequestException("Пользователь не авторизован.");
        }

        User user = userRepository.findUserByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Авторизованный пользователь не найден."));

        EnrollmentCallType callType = user.getRole().equals(UserRole.TRAINER) ?
                EnrollmentCallType.TRAINER : EnrollmentCallType.CLIENT;

        if (callType == EnrollmentCallType.CLIENT) {
            if (user.getSubscription() == null || !Boolean.TRUE.equals(user.getSubscription().getIsActive())) {
                throw new BadRequestException("У вас нет активной подписки.");
            }

            Long remainingTrainings = user.getSubscription().getPersonalTrainingCount();
            if (remainingTrainings == null || remainingTrainings <= 0) {
                throw new BadRequestException("У вас не осталось доступных тренировок.");
            }
        }

        TrainingSession session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Занятие с ID " + sessionId + " не найдено."));

        if (session.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Невозможно записаться на занятие, которое уже началось или прошло.");
        }

        if (enrollmentRepository.existsByUserAndTrainingSessionAndStatusIn(user, session,
                Arrays.asList(EnrollmentStatus.CONFIRMED, EnrollmentStatus.PENDING))) {
            throw new BadRequestException("Вы уже записаны на это занятие или находитесь в листе ожидания.");
        }

        EnrollmentStatus status;
        if (session.getCurrentParticipants() < session.getMaxParticipants()) {
            session.setCurrentParticipants(session.getCurrentParticipants() + 1);
            status = callType == EnrollmentCallType.TRAINER ? EnrollmentStatus.CONFIRMED : EnrollmentStatus.PENDING;

            if (callType == EnrollmentCallType.CLIENT) {
                user.getSubscription().setPersonalTrainingCount(user.getSubscription().getPersonalTrainingCount() - 1);
                userRepository.save(user);
            }
        } else {
            status = EnrollmentStatus.PENDING;
        }

        trainingSessionRepository.save(session);

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .trainingSession(session)
                .status(status)
                .enrollmentCallType(callType)
                .build();

        enrollment = enrollmentRepository.save(enrollment);
        return EnrollmentDto.fromEntity(enrollment);
    }

    @Transactional
    public void approveEnrollment(Long enrollmentId) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (currentUsername == null) {
            throw new BadRequestException("Пользователь не авторизован.");
        }

        User currentUser = userRepository.findUserByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Авторизованный пользователь не найден."));

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new NotFoundException("Запись с ID " + enrollmentId + " не найдена."));

        if (enrollment.getStatus() != EnrollmentStatus.PENDING) {
            throw new BadRequestException("Можно подтверждать только записи в статусе PENDING.");
        }

        TrainingSession session = enrollment.getTrainingSession();
        if (session.getCurrentParticipants() >= session.getMaxParticipants()) {
            throw new BadRequestException("Достигнуто максимальное количество участников.");
        }

        if (currentUser.getRole().equals(UserRole.TRAINER)) {
            // Тренер подтверждает записи клиентов
            if (!session.getTrainer().getId().equals(currentUser.getId())) {
                throw new BadRequestException("Вы не можете подтверждать записи для чужих тренировок.");
            }
            if (enrollment.getEnrollmentCallType() != EnrollmentCallType.CLIENT) {
                throw new BadRequestException("Тренер может подтверждать только записи клиентов.");
            }
        } else if (currentUser.getRole().equals(UserRole.DEFAULT_USER)) {
            // Клиент подтверждает записи, созданные тренером
            if (!enrollment.getUser().getId().equals(currentUser.getId())) {
                throw new BadRequestException("Вы можете подтверждать только свои записи.");
            }
            if (enrollment.getEnrollmentCallType() != EnrollmentCallType.TRAINER) {
                throw new BadRequestException("Клиент может подтверждать только записи, созданные тренером.");
            }
            // Проверка подписки клиента
            if (currentUser.getSubscription() == null || !currentUser.getSubscription().getIsActive()) {
                throw new BadRequestException("У вас нет активной подписки.");
            }
            Long currentCount = currentUser.getSubscription().getPersonalTrainingCount();
            if (currentCount <= 0) {
                throw new BadRequestException("У вас не осталось доступных тренировок.");
            }
            currentUser.getSubscription().setPersonalTrainingCount(currentCount - 1);
            userRepository.save(currentUser);
        } else {
            throw new BadRequestException("Недостаточно прав для подтверждения записи.");
        }

        enrollment.setStatus(EnrollmentStatus.CONFIRMED);
        session.setCurrentParticipants(session.getCurrentParticipants() + 1);

        enrollmentRepository.save(enrollment);
        trainingSessionRepository.save(session);
    }

    @Transactional
    public EnrollmentDto assignUserToSession(Long sessionId, Long userId) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (currentUsername == null) {
            throw new BadRequestException("Пользователь не авторизован.");
        }

        User currentUser = userRepository.findUserByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Авторизованный пользователь не найден."));

        if (!currentUser.getRole().equals(UserRole.TRAINER)) {
            throw new BadRequestException("Только тренер может добавлять пользователей в тренировку.");
        }

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден."));

        TrainingSession session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Занятие с ID " + sessionId + " не найдено."));

        if (!session.getTrainer().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Вы не можете добавлять пользователей в чужие тренировки.");
        }

        if (session.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Невозможно добавить пользователя на занятие, которое уже началось или прошло.");
        }

        if (enrollmentRepository.existsByUserAndTrainingSessionAndStatusIn(targetUser, session,
                Arrays.asList(EnrollmentStatus.CONFIRMED, EnrollmentStatus.PENDING))) {
            throw new BadRequestException("Пользователь уже записан на это занятие или находится в листе ожидания.");
        }

        if (session.getCurrentParticipants() >= session.getMaxParticipants()) {
            throw new BadRequestException("Достигнуто максимальное количество участников.");
        }

        Enrollment enrollment = Enrollment.builder()
                .user(targetUser)
                .trainingSession(session)
                .status(EnrollmentStatus.PENDING) // Устанавливаем PENDING для подтверждения клиентом
                .enrollmentCallType(EnrollmentCallType.TRAINER)
                .build();

        enrollment = enrollmentRepository.save(enrollment);
        return EnrollmentDto.fromEntity(enrollment);
    }

    @Transactional
    public void cancelEnrollment(Long enrollmentId) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (currentUsername == null) {
            throw new BadRequestException("Пользователь не авторизован.");
        }

        User currentUser = userRepository.findUserByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Авторизованный пользователь не найден."));

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new NotFoundException("Запись с ID " + enrollmentId + " не найдена."));

        if (!enrollment.getUser().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(UserRole.TRAINER)) {
            throw new BadRequestException("Вы не можете отменить запись другого пользователя.");
        }

        if (currentUser.getRole().equals(UserRole.TRAINER) &&
                !enrollment.getTrainingSession().getTrainer().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Вы не можете отменить запись для чужой тренировки.");
        }

        if (enrollment.getTrainingSession().getStartTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Невозможно отменить запись на занятие, которое уже началось или прошло.");
        }

        if (enrollment.getStatus() == EnrollmentStatus.CANCELLED) {
            throw new BadRequestException("Запись уже отменена.");
        }

        if (enrollment.getStatus() == EnrollmentStatus.CONFIRMED) {
            TrainingSession session = enrollment.getTrainingSession();
            session.setCurrentParticipants(session.getCurrentParticipants() - 1);
            trainingSessionRepository.save(session);

            if (enrollment.getEnrollmentCallType() == EnrollmentCallType.CLIENT &&
                    currentUser.getSubscription() != null) {
                Long currentCount = currentUser.getSubscription().getPersonalTrainingCount();
                currentUser.getSubscription().setPersonalTrainingCount(currentCount + 1);
                userRepository.save(currentUser);
            }

            List<Enrollment> waitlist = enrollmentRepository.findAllByTrainingSessionAndStatusOrderByEnrollmentTimeAsc(
                    session, EnrollmentStatus.PENDING);
            if (!waitlist.isEmpty()) {
                Enrollment nextInWaitlist = waitlist.getFirst();
                nextInWaitlist.setStatus(EnrollmentStatus.CONFIRMED);
                if (nextInWaitlist.getEnrollmentCallType() == EnrollmentCallType.CLIENT) {
                    User client = nextInWaitlist.getUser();
                    if (client.getSubscription() != null && client.getSubscription().getIsActive()) {
                        Long currentCount = client.getSubscription().getPersonalTrainingCount();
                        if (currentCount > 0) {
                            client.getSubscription().setPersonalTrainingCount(currentCount - 1);
                            userRepository.save(client);
                        }
                    }
                }
                enrollmentRepository.save(nextInWaitlist);
            }
        }

        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        enrollmentRepository.save(enrollment);
    }

    @Transactional(readOnly = true)
    public EnrollmentListDto getMyEnrollments() {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (currentUsername == null) {
            throw new BadRequestException("Пользователь не авторизован.");
        }

        User user = userRepository.findUserByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Авторизованный пользователь не найден."));

        List<EnrollmentDto> enrollmentDtos = enrollmentRepository.findAllByUser(user).stream()
                .map(EnrollmentDto::fromEntity)
                .collect(Collectors.toList());

        return new EnrollmentListDto(enrollmentDtos);
    }

    @Transactional(readOnly = true)
    public EnrollmentListDto getSessionEnrollments(Long sessionId) {
        TrainingSession session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Занятие с ID " + sessionId + " не найдено."));

        List<EnrollmentDto> enrollmentDtos = enrollmentRepository.findAllByTrainingSession(session).stream()
                .map(EnrollmentDto::fromEntity)
                .collect(Collectors.toList());

        return new EnrollmentListDto(enrollmentDtos);
    }

    public Boolean isTrainingSessionAssigned(Long trainingSessionId) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (currentUsername == null) {
            throw new BadRequestException("Пользователь не авторизован");
        }

        User currentUser = userRepository.findUserByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return enrollmentRepository.existsByUserIdAndTrainingSessionIdAndStatusNot(
                currentUser.getId(), trainingSessionId, EnrollmentStatus.CANCELLED);
    }

    @Transactional(readOnly = true)
    public EnrollmentListDto getTrainerEnrollments() {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (currentUsername == null) {
            throw new BadRequestException("Пользователь не авторизован.");
        }

        User currentUser = userRepository.findUserByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Авторизованный пользователь не найден."));

        if (!currentUser.getRole().equals(UserRole.TRAINER)) {
            throw new BadRequestException("Только тренеры могут получить список заявок на свои тренировки.");
        }

        List<EnrollmentDto> enrollmentDtos = enrollmentRepository.findAllByTrainerAndEnrollmentCallType(currentUser, EnrollmentCallType.CLIENT).stream()
                .map(EnrollmentDto::fromEntity)
                .collect(Collectors.toList());

        return new EnrollmentListDto(enrollmentDtos);
    }
}