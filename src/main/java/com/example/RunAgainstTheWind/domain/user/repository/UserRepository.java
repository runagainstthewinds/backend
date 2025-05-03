package com.example.RunAgainstTheWind.domain.user.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.dto.user.UserDTO;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    @Query("""
      SELECT new com.example.RunAgainstTheWind.dto.user.UserDTO(
        u.userId,
        u.username,
        u.email,
        u.googleCalendarToken,
        u.stravaToken,
        u.stravaRefreshToken
      )
      FROM User u
      WHERE u.username = :username
    """)
    UserDTO getUserByUsername(@Param("username") String username);
}
