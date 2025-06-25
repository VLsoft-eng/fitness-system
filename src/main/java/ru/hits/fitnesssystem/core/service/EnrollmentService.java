package ru.hits.fitnesssystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.fitnesssystem.core.entity.Enrollment;
import ru.hits.fitnesssystem.core.entity.TrainingSession;
import ru.hits.fitnesssystem.core.entity.User;
import ru.hits.fitnesssystem.core.enumeration.EnrollmentStatus;
import ru.hits.fitnesssystem.core.enumeration.TrainingSessionType;
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

        TrainingSession session = trainingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Занятие с ID " + sessionId + " не найдено."));

        if (session.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Невозможно записаться на занятие, которое уже началось или прошло.");
        }

        if (enrollmentRepository.existsByUserAndTrainingSessionAndStatusIn(user, session,
                Arrays.asList(EnrollmentStatus.CONFIRMED, EnrollmentStatus.WAITLIST))) {
            throw new BadRequestException("Вы уже записаны на это занятие или находитесь в листе ожидания.");
        }

        EnrollmentStatus status;
        if (session.getType() == TrainingSessionType.GROUP && session.getCurrentParticipants() < session.getMaxParticipants()) {
            status = EnrollmentStatus.CONFIRMED;
            session.setCurrentParticipants(session.getCurrentParticipants() + 1);
        } else if (session.getType() == TrainingSessionType.PERSONAL) {
            if (session.getCurrentParticipants() < session.getMaxParticipants()) {
                status = EnrollmentStatus.CONFIRMED;
                session.setCurrentParticipants(session.getCurrentParticipants() + 1);
            } else {
                throw new BadRequestException("Персональная тренировка уже занята.");
            }
        } else {
            status = EnrollmentStatus.WAITLIST;
        }

        trainingSessionRepository.save(session);

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .trainingSession(session)
                .status(status)
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

        if (!enrollment.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Вы не можете отменить запись другого пользователя.");
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

            List<Enrollment> waitlist = enrollmentRepository.findAllByTrainingSessionAndStatusOrderByEnrollmentTimeAsc(session, EnrollmentStatus.WAITLIST);
            if (!waitlist.isEmpty()) {
                Enrollment nextInWaitlist = waitlist.get(0);
                nextInWaitlist.setStatus(EnrollmentStatus.CONFIRMED);
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
}