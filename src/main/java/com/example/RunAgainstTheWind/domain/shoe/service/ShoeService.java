package com.example.RunAgainstTheWind.domain.shoe.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.RunAgainstTheWind.application.validation.ValidationService;
import com.example.RunAgainstTheWind.domain.shoe.model.Shoe;
import com.example.RunAgainstTheWind.domain.shoe.repository.ShoeRepository;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.dto.shoe.ShoeDTO;

@Service
public class ShoeService {

    @Autowired
    private ShoeRepository shoeRepository;

    @Autowired
    private ValidationService v;

    public List<ShoeDTO> getShoesByUserID(UUID userId) { 
        v.validateUserExists(userId);
        return shoeRepository.getShoesByUserId(userId);
    }

    public ShoeDTO createShoe(UUID userId, ShoeDTO shoeDTO) {
        User user = v.validateUserExistsAndReturn(userId);

        Shoe shoe = new Shoe();
        shoe.setBrand(shoeDTO.getBrand());
        shoe.setModel(shoeDTO.getModel());
        shoe.setSize(shoeDTO.getSize());
        shoe.setTotalMileage(shoeDTO.getTotalMileage());
        shoe.setPrice(shoeDTO.getPrice());
        shoe.setUser(user);
        Shoe savedShoe = shoeRepository.save(shoe);

        shoeDTO.setShoeId(savedShoe.getShoeId());   
        shoeDTO.setUserId(user.getUserId());
        return shoeDTO;
    }

    public void deleteShoe(Long shoeId) {
        if (!shoeRepository.existsById(shoeId)) throw new RuntimeException("Shoe not found with id: " + shoeId);
        shoeRepository.deleteById(shoeId);
    }

    public ShoeDTO updateShoe(Long shoeId, ShoeDTO shoeDTO) {
        Shoe shoe = shoeRepository.findById(shoeId).orElseThrow(() -> new RuntimeException("Shoe not found with id: " + shoeId));

        if (shoeDTO.getBrand() != null) {
            shoe.setBrand(shoeDTO.getBrand());
        }
        if (shoeDTO.getModel() != null) {
            shoe.setModel(shoeDTO.getModel());
        }
        if (shoeDTO.getSize() != null) {
            shoe.setSize(shoeDTO.getSize());
        }
        if (shoeDTO.getTotalMileage() != null) {
            shoe.setTotalMileage(shoeDTO.getTotalMileage());
        }
        if (shoeDTO.getPrice() != null) {
            shoe.setPrice(shoeDTO.getPrice());
        }
        Shoe updatedShoe = shoeRepository.save(shoe);

        ShoeDTO updatedDTO = new ShoeDTO();
        updatedDTO.setShoeId(updatedShoe.getShoeId());
        updatedDTO.setBrand(updatedShoe.getBrand());
        updatedDTO.setModel(updatedShoe.getModel());
        updatedDTO.setSize(updatedShoe.getSize());
        updatedDTO.setTotalMileage(updatedShoe.getTotalMileage());
        updatedDTO.setPrice(updatedShoe.getPrice());
        updatedDTO.setUserId(shoe.getUser().getUserId());
        return updatedDTO;
    }
}
