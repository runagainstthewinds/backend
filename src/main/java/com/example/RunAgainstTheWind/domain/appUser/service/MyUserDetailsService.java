package com.example.RunAgainstTheWind.domain.appUser.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.RunAgainstTheWind.application.auth.UserPrincipal;
import com.example.RunAgainstTheWind.application.user.model.Users;
import com.example.RunAgainstTheWind.application.user.repository.UserRepo;
import com.example.RunAgainstTheWind.domain.appUser.model.AppUser;
import com.example.RunAgainstTheWind.domain.appUser.repository.AppUserRepository;

/*
 * Retrieve user information from the database
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final UserRepo userRepo;

    @Autowired
    public MyUserDetailsService(AppUserRepository appUserRepository, UserRepo userRepo) {
        this.appUserRepository = appUserRepository;
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Optional<AppUser> appUser = appUserRepository.findByEmail(identifier);
        if (appUser.isPresent()) {
            return appUser.get();
        }
        Users user = userRepo.findByUsername(identifier);
        if (user == null) {
            throw new UsernameNotFoundException("No user found with email/username: " + identifier);
        }
        return new UserPrincipal(user);
    }
}