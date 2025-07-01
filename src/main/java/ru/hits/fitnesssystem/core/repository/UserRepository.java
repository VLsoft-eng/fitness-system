package ru.hits.fitnesssystem.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.hits.fitnesssystem.core.entity.User;
import ru.hits.fitnesssystem.core.enumeration.UserRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsername(String username);
    boolean existsUserByUsername(String username);
    List<User> findAllByRole(UserRole role);
    @Query("SELECT u FROM User u WHERE u.role = :role AND (" +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(CONCAT(u.lastName, ' ', u.firstName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')))"
    )
    List<User> findAllByRoleAndSearchTerm(@Param("role") UserRole role,
                                          @Param("searchTerm") String searchTerm);
}
