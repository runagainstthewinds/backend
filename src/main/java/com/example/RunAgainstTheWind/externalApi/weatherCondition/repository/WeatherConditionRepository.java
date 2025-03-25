package com.example.RunAgainstTheWind.externalApi.weatherCondition.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.RunAgainstTheWind.externalApi.weatherCondition.model.WeatherCondition;

@Repository
public interface WeatherConditionRepository extends JpaRepository<WeatherCondition, Long> {
    
}
