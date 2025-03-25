package com.example.RunAgainstTheWind.domain.appUserDetails.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.RunAgainstTheWind.domain.appUserDetails.model.AppUserDetails;

@Repository
public interface AppUserDetailsRepository extends JpaRepository<AppUserDetails, Long> {

}
