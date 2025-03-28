package com.example.RunAgainstTheWind.domain.appUser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.RunAgainstTheWind.application.auth.UserPrincipal;
import com.example.RunAgainstTheWind.domain.appUser.model.AppUser;
import com.example.RunAgainstTheWind.domain.appUser.repository.AppUserRepository;

/*
 * Retrieve user information from the database
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepo;

    @Autowired
    public MyUserDetailsService(AppUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        AppUser user = userRepo.findByUsername(identifier);
        if (user == null) {
            throw new UsernameNotFoundException("No user found with email/username: " + identifier);
        }
        return new UserPrincipal(user);
    }
}