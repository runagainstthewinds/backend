package com.example.RunAgainstTheWind.dto.shoe;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoeDTO {
    private Long shoeId;
    private String brand;
    private String model;
    private Double size;
    private Double totalMileage;
    private Double price;
    private UUID userId;
}