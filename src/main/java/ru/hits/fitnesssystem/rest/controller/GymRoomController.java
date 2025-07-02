package ru.hits.fitnesssystem.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.hits.fitnesssystem.core.service.GymRoomService;
import ru.hits.fitnesssystem.rest.model.GymRoomCreateDto;
import ru.hits.fitnesssystem.rest.model.GymRoomDto;
import ru.hits.fitnesssystem.rest.model.GymRoomListDto;
import ru.hits.fitnesssystem.rest.model.GymRoomUpdateDto;

@RestController
@RequestMapping("/gym-rooms")
@RequiredArgsConstructor
public class GymRoomController {
    private final GymRoomService gymRoomService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GymRoomDto> createGymRoom(@RequestBody @Validated GymRoomCreateDto createDto) {
        GymRoomDto createdGymRoom = gymRoomService.createGymRoom(createDto);
        return ResponseEntity.status(HttpStatus.OK).body(createdGymRoom);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GymRoomDto> updateGymRoom(
            @PathVariable Long id,
            @RequestBody @Validated GymRoomUpdateDto updateDto) {
        GymRoomDto updatedGymRoom = gymRoomService.updateGymRoom(id, updateDto);
        return ResponseEntity.ok(updatedGymRoom);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGymRoom(@PathVariable Long id) {
        gymRoomService.deleteGymRoom(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GymRoomDto> getGymRoomById(@PathVariable Long id) {
        GymRoomDto gymRoom = gymRoomService.getGymRoomById(id);
        return ResponseEntity.ok(gymRoom);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GymRoomListDto> getAllGymRooms(@RequestParam(required = false) String name) {
        GymRoomListDto gymRooms = gymRoomService.getAllGymRooms(name);
        return ResponseEntity.ok(gymRooms);
    }
}
