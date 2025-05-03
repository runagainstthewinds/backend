package com.example.RunAgainstTheWind.dto.shoe;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShoeDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long shoeId;
    
    private String model;
    private String brand;
    private String color;
    private Double totalMileage;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate date;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID userId;
}