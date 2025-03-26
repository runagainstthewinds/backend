package com.example.RunAgainstTheWind.repositoryTesting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.RunAgainstTheWind.domain.shoe.model.Shoe;
import com.example.RunAgainstTheWind.domain.shoe.repository.ShoeRepository;
import com.example.RunAgainstTheWind.domain.trainingSession.repository.TrainingSessionRepository;

@SpringBootTest
@Transactional
public class ShoeRepositoryTest {
    
    @Autowired
    private ShoeRepository shoeRepository;

    @Autowired
    private TrainingSessionRepository trainingSessionRepository;

    @BeforeEach
    public void setUp() {
        trainingSessionRepository.deleteAll();
        shoeRepository.deleteAll();
    }
    

    @Test
    public void testCreateShoe() {
        // Create new shoe
        Shoe shoe = new Shoe("Nike", "Alphafly", 10.0, 100.0, 250.0);
        Shoe savedShoe = shoeRepository.save(shoe);

        // Check if it exists
        assertNotNull(savedShoe.getShoeId());

        // Check if it is retrievable from the database
        Optional<Shoe> retrievedShoe = shoeRepository.findById(savedShoe.getShoeId());
        assertNotNull(retrievedShoe);

        // Check if the attributes are correct
        assertNotNull(retrievedShoe.get().getBrand());
        assertNotNull(retrievedShoe.get().getModel());
        assertNotNull(retrievedShoe.get().getSize());
        assertNotNull(retrievedShoe.get().getTotalMileage());
        assertNotNull(retrievedShoe.get().getPrice());
        assertEquals("Nike", retrievedShoe.get().getBrand());
        assertEquals("Alphafly", retrievedShoe.get().getModel());
        assertEquals(10.0, retrievedShoe.get().getSize());
        assertEquals(100.0, retrievedShoe.get().getTotalMileage());
        assertEquals(250.0, retrievedShoe.get().getPrice());
    }
}
