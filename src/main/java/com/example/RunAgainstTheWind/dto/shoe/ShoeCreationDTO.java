package com.example.RunAgainstTheWind.dto.shoe;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShoeCreationDTO {

    @NotBlank(message = "Brand is mandatory")
    private String brand;

    @NotBlank(message = "Model is mandatory")
    private String model;

    @NotBlank(message = "Size is mandatory")
    private String size;

    @NotBlank(message = "Total mileage is mandatory")
    private String totalMileage;

    @NotBlank(message = "Price is mandatory")
    private String price;

    public ShoeCreationDTO(String brand, String model, String size, String totalMileage, String price) {
        this.brand = brand;
        this.model = model;
        this.size = size;
        this.totalMileage = totalMileage;
        this.price = price;
    }
}
