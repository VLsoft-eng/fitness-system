package ru.hits.fitnesssystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.fitnesssystem.core.enumeration.TrainingSessionType;
import ru.hits.fitnesssystem.core.repository.TrainingSessionRepository;
import ru.hits.fitnesssystem.rest.model.TrainingSessionStatisticsDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingSessionStatisticsService {
    private final TrainingSessionRepository trainingSessionRepository;

    @Transactional(readOnly = true)
    public TrainingSessionStatisticsDto getTrainingSessionStatistics() {
        LocalDateTime now = LocalDateTime.now();

        Long totalScheduledTrainingSessions = trainingSessionRepository.count();
        Long completedTrainingSessions = trainingSessionRepository.countByEndTimeBefore(now);
        Long activeTrainingSessions = trainingSessionRepository.countByStartTimeBeforeAndEndTimeAfter(now, now);
        Long upcomingTrainingSessions = trainingSessionRepository.countByStartTimeAfter(now);

        Map<String, Long> popularTrainingTypes = new HashMap<>();
        List<Object[]> popularTrainingTypeResults = trainingSessionRepository.countTrainingSessionsByType();
        if (popularTrainingTypeResults != null) {
            popularTrainingTypes = popularTrainingTypeResults.stream()
                    .collect(Collectors.toMap(
                            arr -> ((TrainingSessionType) arr[0]).name(),
                            arr -> (Long) arr[1]
                    ));
        }

        OptionalDouble avgParticipantsOpt = trainingSessionRepository.findAverageParticipantsForCompletedSessions();
        Double averageParticipantsPerSession;

        if (avgParticipantsOpt == null) {
            averageParticipantsPerSession = 0.0;
        } else if (avgParticipantsOpt.isEmpty()) {
            averageParticipantsPerSession = 0.0;
        } else {
            averageParticipantsPerSession = avgParticipantsOpt.getAsDouble();
        }

        Long maxParticipantsPerSession = trainingSessionRepository.findMaxParticipantsForCompletedSessions();
        if (maxParticipantsPerSession == null) {
            maxParticipantsPerSession = 0L;
        }

        Map<String, Long> busiestTrainersBySessionsCount = new HashMap<>();
        List<Object[]> busiestTrainersResults = trainingSessionRepository.findBusiestTrainersByCompletedSessions();
        if (busiestTrainersResults != null) {
            busiestTrainersBySessionsCount = busiestTrainersResults.stream()
                    .collect(Collectors.toMap(
                            arr -> {
                                String firstName = (String) arr[0];
                                String lastName = (String) arr[1];
                                return String.format("%s %s",
                                        (firstName != null ? firstName : "НеизвестноеИмя"),
                                        (lastName != null ? lastName : "НеизвестнаяФамилия"));
                            },
                            arr -> (Long) arr[2]
                    ));
        }

        return TrainingSessionStatisticsDto.builder()
                .totalScheduledTrainingSessions(totalScheduledTrainingSessions)
                .completedTrainingSessions(completedTrainingSessions)
                .activeTrainingSessions(activeTrainingSessions)
                .upcomingTrainingSessions(upcomingTrainingSessions)
                .popularTrainingTypes(popularTrainingTypes)
                .averageParticipantsPerSession(averageParticipantsPerSession)
                .maxParticipantsPerSession(maxParticipantsPerSession)
                .busiestTrainersBySessionsCount(busiestTrainersBySessionsCount)
                .build();
    }
}
