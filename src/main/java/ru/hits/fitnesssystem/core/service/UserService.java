package ru.hits.fitnesssystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.hits.fitnesssystem.core.entity.User;
import ru.hits.fitnesssystem.core.enumeration.UserRole;
import ru.hits.fitnesssystem.core.exception.BadRequestException;
import ru.hits.fitnesssystem.core.exception.NotFoundException;
import ru.hits.fitnesssystem.core.repository.UserRepository;
import ru.hits.fitnesssystem.core.security.JwtTokenProvider;
import ru.hits.fitnesssystem.core.security.SecurityUtils;
import ru.hits.fitnesssystem.rest.model.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenDto register(UserRegistrationDto userRegistrationDto) {
        if (userRepository.existsUserByUsername(userRegistrationDto.username())) {
            throw new BadRequestException("Юзернейм уже занят");
        }

        String encodedPassword = bcryptPasswordEncoder.encode(userRegistrationDto.password());
        User user = User.builder()
                .username(userRegistrationDto.username())
                .hashedPassword(encodedPassword)
                .firstName(userRegistrationDto.firstname())
                .lastName(userRegistrationDto.lastname())
                .gender(userRegistrationDto.gender())
                .role(UserRole.DEFAULT_USER)
                .build();
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getUsername());
        return new TokenDto(token);
    }

    public TokenDto login(UserLoginDto userLoginDto) {
        User user = userRepository.findUserByUsername(userLoginDto.username())
                .orElseThrow(() -> new NotFoundException("Пользователя с такими данными не существует"));

        String token = jwtTokenProvider.generateToken(user.getUsername());

        return new TokenDto(token);
    }

    public UserDto getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с такими данными не существует"));

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getGender(),
                user.getRole(),
                Optional.ofNullable(user.getBirthday())
        );
    }

    public UserDto getMyProfile() {
        String currentUsername = SecurityUtils.getCurrentUsername();

        User user = userRepository.findUserByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Пользователя с такими данными не существует"));

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getGender(),
                user.getRole(),
                Optional.ofNullable(user.getBirthday())
        );
    }

    public void changeUserRole(ChangeUserRoleDto changeUserRoleDto) {
        User user = userRepository.findById(changeUserRoleDto.userId())
                .orElseThrow(() -> new NotFoundException("Пользователя с такими данными не существует"));

        if (changeUserRoleDto.newRole() == UserRole.ADMIN) {
            throw new BadRequestException("Роль ADMIN не может быть установлена");
        }

        user.setRole(changeUserRoleDto.newRole());
        userRepository.save(user);
    }

    public UserListDto getAllUsers() {
        List<UserDto> userDtos =  userRepository.findAll().stream().map(user -> new UserDto(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getGender(),
                user.getRole(),
                Optional.ofNullable(user.getBirthday())
        )).toList();

        return new UserListDto(userDtos);
    }
}
