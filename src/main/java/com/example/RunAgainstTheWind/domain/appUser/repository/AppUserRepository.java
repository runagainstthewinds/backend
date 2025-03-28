package com.example.RunAgainstTheWind.domain.appUser.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.RunAgainstTheWind.domain.appUser.model.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    AppUser findByUsername(String username);
}
