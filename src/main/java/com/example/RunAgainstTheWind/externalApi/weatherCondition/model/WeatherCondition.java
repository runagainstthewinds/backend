package com.example.RunAgainstTheWind.externalApi.weatherCondition.model;

import com.example.RunAgainstTheWind.enumeration.Weather;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "weather_condition")
@Data
public class WeatherCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long weatherConditionId;

    private Weather weather;
    private Double temperature;
    private Double windSpeed;

    public WeatherCondition() {}
    
    public WeatherCondition(Weather weather, Double temperature, Double windSpeed) {
        this.weather = weather;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
    }
}
