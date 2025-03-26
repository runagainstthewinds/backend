package com.example.RunAgainstTheWind.repositoryTesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.RunAgainstTheWind.enumeration.Weather;
import com.example.RunAgainstTheWind.externalApi.weatherCondition.model.WeatherCondition;
import com.example.RunAgainstTheWind.externalApi.weatherCondition.repository.WeatherConditionRepository;

@SpringBootTest
@Transactional
public class WeatherConditionRepositoryTest {
    
    @Autowired
    private WeatherConditionRepository weatherConditionRepository;

    @BeforeEach
    public void setUp() {
        weatherConditionRepository.deleteAll();
    }

    @Test
    public void testCreateWeatherCondition() {
        // Create new weather condition
        WeatherCondition weatherCondition = new WeatherCondition(Weather.SUN, 20.0, 10.0);
        weatherConditionRepository.save(weatherCondition);

        // Check if it exists
        assertNotNull(weatherCondition.getWeatherConditionId());

        // Check if it is retrievable from the database
        WeatherCondition retrievedWeatherCondition = weatherConditionRepository.findById(weatherCondition.getWeatherConditionId()).get();
        assertNotNull(retrievedWeatherCondition);

        // Check if the attributes are correct
        assertEquals(Weather.SUN, retrievedWeatherCondition.getWeather());
        assertEquals(20.0, retrievedWeatherCondition.getTemperature());
        assertEquals(10.0, retrievedWeatherCondition.getWindSpeed());
    }
}
