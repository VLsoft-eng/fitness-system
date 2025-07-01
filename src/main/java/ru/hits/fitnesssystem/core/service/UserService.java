package ru.hits.fitnesssystem.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.hits.fitnesssystem.core.entity.Subscription;
import ru.hits.fitnesssystem.core.entity.User;
import ru.hits.fitnesssystem.core.enumeration.UserRole;
import ru.hits.fitnesssystem.core.exception.BadRequestException;
import ru.hits.fitnesssystem.core.exception.NotFoundException;
import ru.hits.fitnesssystem.core.repository.SubscriptionRepository;
import ru.hits.fitnesssystem.core.repository.UserRepository;
import ru.hits.fitnesssystem.core.security.JwtTokenProvider;
import ru.hits.fitnesssystem.core.security.SecurityUtils;
import ru.hits.fitnesssystem.rest.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SubscriptionRepository subscriptionRepository;

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
                .avatarBase64(userRegistrationDto.avatarBase64())
                .build();

        Subscription subscription = Subscription.builder()
                .subscriber(user)
                .build();

        user.setSubscription(subscription); // Set the bidirectional relationship
        subscriptionRepository.save(subscription); // Save subscription first
        userRepository.save(user); // Save user with the subscription

        String token = jwtTokenProvider.generateToken(user.getUsername());
        return new TokenDto(token);
    }

    public TokenDto login(UserLoginDto userLoginDto) {
        User user = userRepository.findUserByUsername(userLoginDto.username())
                .orElseThrow(() -> new NotFoundException("Пользователя с такими данными не существует"));

        if (!bcryptPasswordEncoder.matches(userLoginDto.password(), user.getHashedPassword())) {
            throw new BadRequestException("Неверный логин или пароль");
        }

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
                Optional.ofNullable(user.getBirthday()),
                user.getAvatarBase64()
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
                Optional.ofNullable(user.getBirthday()),
                user.getAvatarBase64()
        );
    }

    public UserDto updateMyProfile(UserUpdateDto userUpdateDto) {
        String currentUsername = SecurityUtils.getCurrentUsername();

        User user = userRepository.findUserByUsername(currentUsername)
                .orElseThrow(() -> new NotFoundException("Пользователя с такими данными не существует"));

        if (userUpdateDto.firstname() != null) {
            user.setFirstName(userUpdateDto.firstname());
        }
        if (userUpdateDto.lastname() != null) {
            user.setLastName(userUpdateDto.lastname());
        }
        if (userUpdateDto.gender() != null) {
            user.setGender(userUpdateDto.gender());
        }
        if (userUpdateDto.birthday() != null) {
            user.setBirthday(userUpdateDto.birthday());
        }
        if (userUpdateDto.avatarBase64() != null) {
            user.setAvatarBase64(userUpdateDto.avatarBase64());
        } else {
            user.setAvatarBase64(null);
        }

        userRepository.save(user);
        return getMyProfile();
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
                Optional.ofNullable(user.getBirthday()),
                user.getAvatarBase64()
        )).toList();

        return new UserListDto(userDtos);
    }

    public UserListDto getAllTrainers(String query) {
        Set<User> uniqueTrainers = new HashSet<>();

        if (query != null && !query.trim().isEmpty()) {
            String trimmedQuery = query.trim();
            String[] queryWords = trimmedQuery.split("\\s+");

            for (String word : queryWords) {
                uniqueTrainers.addAll(userRepository.findAllByRoleAndSearchTerm(UserRole.TRAINER, word));
            }
            uniqueTrainers.addAll(userRepository.findAllByRoleAndSearchTerm(UserRole.TRAINER, trimmedQuery));

        } else {
            uniqueTrainers.addAll(userRepository.findAllByRole(UserRole.TRAINER));
        }

        List<UserDto> trainerDtos = uniqueTrainers.stream().map(user -> new UserDto(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getGender(),
                user.getRole(),
                Optional.ofNullable(user.getBirthday()),
                user.getAvatarBase64()
        )).toList();

        return new UserListDto(trainerDtos);
    }
}