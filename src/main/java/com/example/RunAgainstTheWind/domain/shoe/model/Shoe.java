package com.example.RunAgainstTheWind.domain.shoe.model;

import java.util.List;

import com.example.RunAgainstTheWind.domain.appUser.model.AppUser;
import com.example.RunAgainstTheWind.domain.trainingSession.model.TrainingSession;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "shoe")
@Getter
@Setter
public class Shoe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shoeId;

    private String brand;
    private String model;
    private Double size;
    private Double totalMileage;
    private Double price;

    public Shoe() {}

    public Shoe(String brand, String model, Double size, Double totalMileage, Double price) {
        this.brand = brand;
        this.model = model;
        this.size = size;
        this.totalMileage = totalMileage;
        this.price = price;
    }

    @ManyToOne
    @JoinColumn(name = "id")
    private AppUser appUser;

    @OneToMany(mappedBy = "shoe")
    private List<TrainingSession> trainingSessions;
}
