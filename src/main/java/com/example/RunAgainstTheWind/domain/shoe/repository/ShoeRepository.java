package com.example.RunAgainstTheWind.domain.shoe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.RunAgainstTheWind.domain.shoe.model.Shoe;
import com.example.RunAgainstTheWind.domain.user.model.User;

@Repository
public interface ShoeRepository extends JpaRepository<Shoe, Long> {
    List<Shoe> findByUser(User user);
}
