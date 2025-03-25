package com.example.RunAgainstTheWind.domain.shoe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.RunAgainstTheWind.domain.shoe.model.Shoe;

@Repository
public interface ShoeRepository extends JpaRepository<Shoe, Long> {
    
}
