package com.example.RunAgainstTheWind.domain.race.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.RunAgainstTheWind.domain.race.model.Race;

@Repository
public interface RaceRepository extends JpaRepository<Race, Long> {
    
}
