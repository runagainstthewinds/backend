package com.example.RunAgainstTheWind.dto.shoe;

import java.util.UUID;

import lombok.Data;

@Data
public class ShoeDTO {
    private Long shoeId;
    private String brand;
    private String model;
    private Double size;
    private Double totalMileage;
    private Double price;
    private UUID userId;
}