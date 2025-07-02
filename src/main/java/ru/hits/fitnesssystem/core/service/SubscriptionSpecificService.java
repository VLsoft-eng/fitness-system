package ru.hits.fitnesssystem.core.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.fitnesssystem.core.entity.SubscriptionSpecific;
import ru.hits.fitnesssystem.core.exception.NotFoundException;
import ru.hits.fitnesssystem.core.repository.SubscriptionSpecificRepository;
import ru.hits.fitnesssystem.rest.model.SubscriptionSpecificCreateDto;
import ru.hits.fitnesssystem.rest.model.SubscriptionSpecificListDto;
import ru.hits.fitnesssystem.rest.model.SubscriptionSpecificResponseDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionSpecificService {

    private final SubscriptionSpecificRepository repository;

    public SubscriptionSpecificService(SubscriptionSpecificRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public SubscriptionSpecificResponseDto create(SubscriptionSpecificCreateDto createDto) {
        SubscriptionSpecific entity = SubscriptionSpecific.builder()
                .name(createDto.name())
                .description(createDto.description())
                .personalTrainingCount(createDto.personalTrainingCount() != null ? createDto.personalTrainingCount() : 0L)
                .subscriptionDaysCount(createDto.subscriptionDaysCount() != null ? createDto.subscriptionDaysCount() : 0L)
                .build();
        SubscriptionSpecific saved = repository.save(entity);
        return toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public SubscriptionSpecificResponseDto getById(Long id) {
        SubscriptionSpecific entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Subscription not found with id: " + id));
        return toResponseDto(entity);
    }

    @Transactional(readOnly = true)
    public SubscriptionSpecificListDto getAll() {
        List<SubscriptionSpecificResponseDto> subscriptions = repository.findAll().stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
        return new SubscriptionSpecificListDto(subscriptions);
    }

    @Transactional(readOnly = true)
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private SubscriptionSpecificResponseDto toResponseDto(SubscriptionSpecific entity) {
        return new SubscriptionSpecificResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPersonalTrainingCount(),
                entity.getSubscriptionDaysCount()
        );
    }
}