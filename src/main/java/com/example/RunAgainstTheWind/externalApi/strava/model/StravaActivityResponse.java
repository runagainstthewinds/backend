package com.example.RunAgainstTheWind.externalApi.strava.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.Instant;

@Data
public class StravaActivityResponse {
    private Long id;
    private String name;
    private Float distance;
    
    @JsonProperty("moving_time")
    private Integer movingTime;

    @JsonProperty("elapsed_time")
    private Integer elapsedTime;
    
    @JsonProperty("total_elevation_gain")
    private Float totalElevationGain;
    
    private String type;
    
    @JsonProperty("start_date")
    private Instant startDate;
    
    @JsonProperty("average_speed")
    private Float averageSpeed;
    
    @JsonProperty("max_speed")
    private Float maxSpeed;
    
    @JsonProperty("average_heartrate")
    private Float averageHeartrate;
    
    @JsonProperty("max_heartrate")
    private Float maxHeartrate;
}