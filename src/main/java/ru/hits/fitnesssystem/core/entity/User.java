package ru.hits.fitnesssystem.core.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.hits.fitnesssystem.core.enumeration.Gender;
import ru.hits.fitnesssystem.core.enumeration.UserRole;

import java.time.LocalDate;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "password", nullable = false)
    private String hashedPassword;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "avatar_base64", columnDefinition = "TEXT")
    private String avatarBase64;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role = UserRole.DEFAULT_USER;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

}
