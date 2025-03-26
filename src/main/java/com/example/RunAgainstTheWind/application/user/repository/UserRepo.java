package com.example.RunAgainstTheWind.application.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.RunAgainstTheWind.application.user.model.Users;

public interface UserRepo extends JpaRepository<Users, Integer> {
    Users findByUsername(String username);
}
