package com.example.RunAgainstTheWind.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class StravaConfig {
    
    @Value("${strava.client.id}")
    private String clientId;
    
    @Value("${strava.client.secret}")
    private String clientSecret;
    
    @Value("${strava.redirect.uri}")
    private String redirectUri;
    
    @Bean
    public WebClient stravaWebClient() {
        return WebClient.builder()
                .baseUrl("https://www.strava.com/api/v3")
                .build();
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }
    
    public String getRedirectUri() {
        return redirectUri;
    }
}
