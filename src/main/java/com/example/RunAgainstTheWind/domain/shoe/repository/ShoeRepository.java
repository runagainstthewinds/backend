package com.example.RunAgainstTheWind.domain.shoe.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.RunAgainstTheWind.domain.shoe.model.Shoe;
import com.example.RunAgainstTheWind.dto.shoe.ShoeDTO;

@Repository
public interface ShoeRepository extends JpaRepository<Shoe, Long> {
    
    // Find all the shoes of a user by their UUID
    @Query("""
        SELECT new com.example.RunAgainstTheWind.dto.shoe.ShoeDTO(
            s.shoeId,
            s.brand,
            s.model,
            s.size,
            s.totalMileage,
            s.price,
            s.user.userId
        )
        FROM Shoe s
        WHERE s.user.userId = :userUUID
    """)
    List<ShoeDTO> getShoesByUserId(@Param("userUUID") UUID userUUID);
}
