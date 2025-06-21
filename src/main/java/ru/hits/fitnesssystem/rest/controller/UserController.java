package ru.hits.fitnesssystem.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.hits.fitnesssystem.core.service.UserService;
import ru.hits.fitnesssystem.rest.model.*;

// bla bla bla

@RequestMapping("/user")
@RequiredArgsConstructor
@RestController()
public class UserController {
    private final UserService userService;

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}/profile")
    public UserDto getUserProfile(@PathVariable("id") Long id) {
        return userService.getUserProfile(id);
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/my-profile")
    public UserDto getMyProfile() {
        return userService.getMyProfile();
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/get-all")
    @PreAuthorize("hasRole('ADMIN')")
    public UserListDto getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation
    @PostMapping("/register")
    public TokenDto register(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        return userService.register(userRegistrationDto);
    }

    @Operation
    @PostMapping("/login")
    public TokenDto login(@Valid @RequestBody UserLoginDto userLoginDto) {
        return userService.login(userLoginDto);
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/my-profile")
    @PreAuthorize("isAuthenticated()")
    public UserDto updateMyProfile(@Valid @RequestBody UserUpdateDto userUpdateDto) {
        return userService.updateMyProfile(userUpdateDto);
    }
}