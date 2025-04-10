package com.example.RunAgainstTheWind.serviceTesting;

import com.example.RunAgainstTheWind.domain.shoe.model.Shoe;
import com.example.RunAgainstTheWind.domain.shoe.repository.ShoeRepository;
import com.example.RunAgainstTheWind.domain.shoe.service.ShoeService;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
import com.example.RunAgainstTheWind.dto.shoe.ShoeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoeServiceTest {

    @Mock
    private ShoeRepository shoeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ShoeService shoeService;

    private User testUser;
    private Shoe testShoe;
    private ShoeDTO testShoeDTO;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new User();
        testUser.setUserId(testUserId);

        testShoe = new Shoe("Nike", "Air Zoom", 10.5, 0.0, 120.0);
        testShoe.setShoeId(1L);
        testShoe.setUser(testUser);

        testShoeDTO = new ShoeDTO();
        testShoeDTO.setShoeId(1L);
        testShoeDTO.setBrand("Nike");
        testShoeDTO.setModel("Air Zoom");
        testShoeDTO.setSize(10.5);
        testShoeDTO.setTotalMileage(0.0);
        testShoeDTO.setPrice(120.0);
        testShoeDTO.setUserId(testUserId);
    }

    @Test
    void getShoesByUser_Success() {
        // Arrange
        List<Shoe> shoes = Arrays.asList(testShoe);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(shoeRepository.findByUser(testUser)).thenReturn(shoes);

        // Act
        List<ShoeDTO> result = shoeService.getShoesByUser(testUserId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testShoeDTO.getBrand(), result.get(0).getBrand());
        assertEquals(testShoeDTO.getModel(), result.get(0).getModel());
        assertEquals(testShoeDTO.getSize(), result.get(0).getSize());
        verify(userRepository).findById(testUserId);
        verify(shoeRepository).findByUser(testUser);
    }

    @Test
    void getShoesByUser_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> shoeService.getShoesByUser(testUserId));
        assertEquals("User not found with UUID: " + testUserId, exception.getMessage());
        verify(userRepository).findById(testUserId);
        verify(shoeRepository, never()).findByUser(any());
    }

    @Test
    void createShoe_Success() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(shoeRepository.save(any(Shoe.class))).thenReturn(testShoe);

        // Act
        ShoeDTO result = shoeService.createShoe(testShoeDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testShoeDTO.getBrand(), result.getBrand());
        assertEquals(testShoeDTO.getModel(), result.getModel());
        assertEquals(testShoeDTO.getSize(), result.getSize());
        verify(userRepository).findById(testUserId);
        verify(shoeRepository).save(any(Shoe.class));
    }

    @Test
    void createShoe_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> shoeService.createShoe(testShoeDTO));
        assertEquals("User not found with id: " + testUserId, exception.getMessage());
        verify(userRepository).findById(testUserId);
        verify(shoeRepository, never()).save(any());
    }

    @Test
    void deleteShoe_Success() {
        // Arrange
        when(shoeRepository.findById(1L)).thenReturn(Optional.of(testShoe));

        // Act
        shoeService.deleteShoe(1L);

        // Assert
        verify(shoeRepository).findById(1L);
        verify(shoeRepository).deleteById(1L);
    }

    @Test
    void deleteShoe_ShoeNotFound_ThrowsException() {
        // Arrange
        when(shoeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> shoeService.deleteShoe(1L));
        assertEquals("Shoe not found with id: 1", exception.getMessage());
        verify(shoeRepository).findById(1L);
        verify(shoeRepository, never()).deleteById(any());
    }

    @Test
    void updateShoe_Success_PartialUpdate() {
        // Arrange
        ShoeDTO updateDTO = new ShoeDTO();
        updateDTO.setTotalMileage(50.5); // Partial update with just mileage
        
        when(shoeRepository.findById(1L)).thenReturn(Optional.of(testShoe));
        when(shoeRepository.save(any(Shoe.class))).thenReturn(testShoe);

        // Act
        ShoeDTO result = shoeService.updateShoe(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testShoeDTO.getShoeId(), result.getShoeId());
        assertEquals(testShoeDTO.getBrand(), result.getBrand()); // Unchanged
        assertEquals(testShoeDTO.getModel(), result.getModel()); // Unchanged
        assertEquals(50.5, result.getTotalMileage()); // Updated
        assertEquals(testShoeDTO.getPrice(), result.getPrice()); // Unchanged
        assertEquals(testShoeDTO.getUserId(), result.getUserId()); // Unchanged
        verify(shoeRepository).findById(1L);
        verify(shoeRepository).save(any(Shoe.class));
        verify(userRepository, never()).findById(any()); // No user update attempted
    }

    @Test
    void updateShoe_Success_FullUpdate() {
        // Arrange
        ShoeDTO updateDTO = new ShoeDTO();
        updateDTO.setBrand("Adidas");
        updateDTO.setModel("Ultraboost");
        updateDTO.setSize(11.0);
        updateDTO.setTotalMileage(100.0);
        updateDTO.setPrice(150.0);
        updateDTO.setUserId(testUserId);

        Shoe updatedShoe = new Shoe("Adidas", "Ultraboost", 11.0, 100.0, 150.0);
        updatedShoe.setShoeId(1L);
        updatedShoe.setUser(testUser);

        when(shoeRepository.findById(1L)).thenReturn(Optional.of(testShoe));
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(shoeRepository.save(any(Shoe.class))).thenReturn(updatedShoe);

        // Act
        ShoeDTO result = shoeService.updateShoe(1L, updateDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getShoeId());
        assertEquals("Adidas", result.getBrand());
        assertEquals("Ultraboost", result.getModel());
        assertEquals(11.0, result.getSize());
        assertEquals(100.0, result.getTotalMileage());
        assertEquals(150.0, result.getPrice());
        assertEquals(testUserId, result.getUserId());
        verify(shoeRepository).findById(1L);
        verify(userRepository).findById(testUserId);
        verify(shoeRepository).save(any(Shoe.class));
    }

    @Test
    void updateShoe_ShoeNotFound_ThrowsException() {
        // Arrange
        ShoeDTO updateDTO = new ShoeDTO();
        updateDTO.setTotalMileage(50.5);

        when(shoeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> shoeService.updateShoe(1L, updateDTO));
        assertEquals("Shoe not found with id: 1", exception.getMessage());
        verify(shoeRepository).findById(1L);
        verify(shoeRepository, never()).save(any());
        verify(userRepository, never()).findById(any());
    }

    @Test
    void updateShoe_UserNotFound_ThrowsException() {
        // Arrange
        UUID newUserId = UUID.randomUUID();
        ShoeDTO updateDTO = new ShoeDTO();
        updateDTO.setUserId(newUserId); // Attempting to update with a new user

        when(shoeRepository.findById(1L)).thenReturn(Optional.of(testShoe));
        when(userRepository.findById(newUserId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> shoeService.updateShoe(1L, updateDTO));
        assertEquals("User not found with id: " + newUserId, exception.getMessage());
        verify(shoeRepository).findById(1L);
        verify(userRepository).findById(newUserId);
        verify(shoeRepository, never()).save(any());
    }
}