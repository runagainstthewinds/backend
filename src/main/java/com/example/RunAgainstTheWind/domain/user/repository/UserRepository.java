package com.example.RunAgainstTheWind.domain.user.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.RunAgainstTheWind.domain.user.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
}
