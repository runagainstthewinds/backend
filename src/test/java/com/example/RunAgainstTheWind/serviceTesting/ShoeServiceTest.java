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
}