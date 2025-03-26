package com.example.RunAgainstTheWind.externalApi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ExternalAPIController {
    
    @GetMapping("/weather")  // Weather API
    public String getWeather() {
        return null;
    }

    @GetMapping("/route")  // Google Maps API
    public String getRoute() {
        return null;
    }

    @GetMapping("/strava")  // Strava API
    public String getStravaData() {
        return null;
    }

    @GetMapping("/calendar")  // Google Calendar API
    public String getCalendar() {
        return null;
    }
}
