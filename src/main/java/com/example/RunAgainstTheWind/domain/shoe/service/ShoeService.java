package com.example.RunAgainstTheWind.domain.shoe.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.RunAgainstTheWind.domain.shoe.model.Shoe;
import com.example.RunAgainstTheWind.domain.shoe.repository.ShoeRepository;
import com.example.RunAgainstTheWind.domain.user.model.User;
import com.example.RunAgainstTheWind.domain.user.repository.UserRepository;
import com.example.RunAgainstTheWind.dto.shoe.ShoeDTO;

@Service
public class ShoeService {

    private final ShoeRepository shoeRepository;
    private final UserRepository userRepository;

    @Autowired
    public ShoeService(ShoeRepository shoeRepository, UserRepository userRepository) {
        this.shoeRepository = shoeRepository;
        this.userRepository = userRepository;
    }

    public List<ShoeDTO> getShoesByUserID(UUID userUUID) {
        return shoeRepository.getShoesByUserId(userUUID);
    }

    public ShoeDTO createShoe(ShoeDTO shoeDTO) {
        Shoe shoe = new Shoe();
        shoe.setBrand(shoeDTO.getBrand());
        shoe.setModel(shoeDTO.getModel());
        shoe.setSize(shoeDTO.getSize());
        shoe.setTotalMileage(shoeDTO.getTotalMileage());
        shoe.setPrice(shoeDTO.getPrice());

        Optional<User> userOptional = userRepository.findById(shoeDTO.getUserId());
        if (userOptional.isPresent()) {
            shoe.setUser(userOptional.get());
        } else {
            throw new RuntimeException("User not found with id: " + shoeDTO.getUserId());
        }

        Shoe savedShoe = shoeRepository.save(shoe);
        shoeDTO.setShoeId(savedShoe.getShoeId());
        return shoeDTO;
    }

    public void deleteShoe(Long shoeId) {
        Optional<Shoe> shoeOptional = shoeRepository.findById(shoeId);
        if (shoeOptional.isPresent()) {
            shoeRepository.deleteById(shoeId);
        } else {
            throw new RuntimeException("Shoe not found with id: " + shoeId);
        }
    }

    public ShoeDTO updateShoe(Long shoeId, ShoeDTO shoeDTO) {
        Optional<Shoe> shoeOptional = shoeRepository.findById(shoeId);
        if (!shoeOptional.isPresent()) {
            throw new RuntimeException("Shoe not found with id: " + shoeId);
        }

        Shoe shoe = shoeOptional.get();

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


        // Valid user check
        if (shoeDTO.getUserId() != null) {
            Optional<User> userOptional = userRepository.findById(shoeDTO.getUserId());
            if (userOptional.isPresent()) {
                shoe.setUser(userOptional.get());
            } else {
                throw new RuntimeException("User not found with id: " + shoeDTO.getUserId());
            }
        }

        Shoe updatedShoe = shoeRepository.save(shoe);

        // Return DTO
        ShoeDTO updatedDTO = new ShoeDTO();
        updatedDTO.setShoeId(updatedShoe.getShoeId());
        updatedDTO.setBrand(updatedShoe.getBrand());
        updatedDTO.setModel(updatedShoe.getModel());
        updatedDTO.setSize(updatedShoe.getSize());
        updatedDTO.setTotalMileage(updatedShoe.getTotalMileage());
        updatedDTO.setPrice(updatedShoe.getPrice());
        updatedDTO.setUserId(updatedShoe.getUser().getUserId());

        return updatedDTO;
    }
}
