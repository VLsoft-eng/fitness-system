package ru.hits.fitnesssystem.rest.model;

import lombok.Builder;

import java.util.Map;

@Builder
public record TrainingSessionStatisticsDto(Long totalScheduledTrainingSessions,
                                           Long completedTrainingSessions,
                                           Long activeTrainingSessions,
                                           Long upcomingTrainingSessions,
                                           Map<String, Long> popularTrainingTypes,
                                           Double averageParticipantsPerSession,
                                           Long maxParticipantsPerSession,
                                           Map<String, Long> busiestTrainersBySessionsCount ) {
}
