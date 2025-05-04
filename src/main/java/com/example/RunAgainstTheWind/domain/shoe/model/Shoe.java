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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shoe")
@Data
@NoArgsConstructor
public class Shoe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shoeId;

    private String model;
    private String brand;
    private String color;
    private Double totalMileage;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    public Shoe(String model, String brand, String color, Double totalMileage, LocalDate date, User user) {
        this.model = model;
        this.brand = brand;
        this.color = color;
        this.totalMileage = totalMileage;
        this.date = date;
        this.user = user;
    }
}
