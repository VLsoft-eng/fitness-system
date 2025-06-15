package ru.hits.fitnesssystem.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.fitnesssystem.core.service.UserService;
import ru.hits.fitnesssystem.rest.model.TokenDto;
import ru.hits.fitnesssystem.rest.model.UserDto;
import ru.hits.fitnesssystem.rest.model.UserLoginDto;
import ru.hits.fitnesssystem.rest.model.UserRegistrationDto;

@RequiredArgsConstructor
@RestController("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/user/{id}/profile")
    public UserDto getUserProfile(@PathVariable("id") Long id) {
        return userService.getUserProfile(id);
    }

    @GetMapping("/user/my-profile")
    public UserDto getMyProfile() {
        return userService.getMyProfile();
    }

    @PostMapping("/user/register")
    public TokenDto register(@Valid UserRegistrationDto userRegistrationDto) {
        return userService.register(userRegistrationDto);
    }

    @PostMapping("/user/login")
    public TokenDto login(@Valid UserLoginDto userLoginDto) {
        return userService.login(userLoginDto);
    }
}
