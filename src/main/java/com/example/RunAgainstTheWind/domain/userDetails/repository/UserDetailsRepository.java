package com.example.RunAgainstTheWind.domain.userDetails.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.RunAgainstTheWind.domain.userDetails.model.UserDetails;
import com.example.RunAgainstTheWind.dto.userDetails.UserDetailsDTO;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {
    // Find the user details by user ID
    @Query("""
        SELECT new com.example.RunAgainstTheWind.dto.userDetails.UserDetailsDTO(
            ud.userDetailsId,
            ud.totalDistance,
            ud.totalDuration,
            ud.weeklyDistance,
            ud.runCount,
            ud.user.userId
        )
        FROM UserDetails ud
        WHERE ud.user.userId = :userUUID
    """)
    UserDetailsDTO getUserDetailsByUserId(@Param("userUUID") UUID userUUID);
}
