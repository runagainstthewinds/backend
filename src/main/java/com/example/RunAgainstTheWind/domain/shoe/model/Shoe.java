package com.example.RunAgainstTheWind.domain.shoe.model;

import java.time.LocalDate;

import com.example.RunAgainstTheWind.domain.user.model.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "shoe")
@Data
public class Shoe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shoeId;

    private String model;
    private String brand;
    private String color;
    private Double totalMileage;
    private LocalDate date;

    public Shoe() {}

    public Shoe(String model, String brand, String color, LocalDate date, Double totalMileage) {
        this.model = model;
        this.brand = brand;
        this.color = color;
        this.date = date;
        this.totalMileage = totalMileage;
    }

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}
