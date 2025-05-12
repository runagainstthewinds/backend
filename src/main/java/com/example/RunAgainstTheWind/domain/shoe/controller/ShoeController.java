package com.example.RunAgainstTheWind.domain.shoe.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.RunAgainstTheWind.domain.shoe.service.ShoeService;
import com.example.RunAgainstTheWind.dto.shoe.ShoeDTO;

@RestController
@RequestMapping("/shoes")
public class ShoeController {

    @Autowired
    private ShoeService shoeService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<ShoeDTO>> getShoesByUser(@PathVariable UUID userId) {
        List<ShoeDTO> shoes = shoeService.getShoesByUserID(userId);
        return new ResponseEntity<>(shoes, HttpStatus.OK);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ShoeDTO> createShoe(@PathVariable UUID userId, @RequestBody ShoeDTO shoeDTO) {
        ShoeDTO createdShoe = shoeService.createShoe(userId, shoeDTO);
        return new ResponseEntity<>(createdShoe, HttpStatus.CREATED);
    }

    @DeleteMapping("/{shoeId}")
    public ResponseEntity<?> deleteShoe(@PathVariable Long shoeId) {
        shoeService.deleteShoe(shoeId);
        return ResponseEntity.ok(Map.of("message", "Shoe with ID " + shoeId + " has been deleted"));
    }

    @PutMapping("/{shoeId}")
    public ResponseEntity<?> updateShoe(@PathVariable Long shoeId, @RequestBody ShoeDTO shoeDTO) {
        ShoeDTO updatedShoe = shoeService.updateShoe(shoeId, shoeDTO);
        return new ResponseEntity<>(updatedShoe, HttpStatus.OK);
    }
}
