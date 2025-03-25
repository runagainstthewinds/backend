package com.example.RunAgainstTheWind.externalApi.route.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.RunAgainstTheWind.externalApi.route.model.Route;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    
}
