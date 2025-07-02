package ru.hits.fitnesssystem.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.hits.fitnesssystem.core.service.SubscriptionSpecificService;
import ru.hits.fitnesssystem.rest.model.SubscriptionSpecificCreateDto;
import ru.hits.fitnesssystem.rest.model.SubscriptionSpecificListDto;
import ru.hits.fitnesssystem.rest.model.SubscriptionSpecificResponseDto;

@RestController
@RequestMapping("/subscriptions-specific")
public class SubscriptionSpecificController {

    private final SubscriptionSpecificService service;

    public SubscriptionSpecificController(SubscriptionSpecificService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<SubscriptionSpecificResponseDto> create(@RequestBody SubscriptionSpecificCreateDto createDto) {
        SubscriptionSpecificResponseDto responseDto = service.create(createDto);
        return ResponseEntity.ok(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionSpecificResponseDto> getById(@PathVariable Long id) {
        SubscriptionSpecificResponseDto responseDto = service.getById(id);
        return ResponseEntity.ok(responseDto);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<SubscriptionSpecificListDto> getAll() {
        SubscriptionSpecificListDto responseDto = service.getAll();
        return ResponseEntity.ok(responseDto);
    }
}