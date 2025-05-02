package com.example.RunAgainstTheWind.dto.shoe;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoeDTO {
    private Long shoeId;
    private String model;
    private String brand;
    private String color;
    private Double totalMileage;
    private LocalDate date;
    private UUID userId;
}