package com.example.RunAgainstTheWind.domain.shoe.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public List<ShoeDTO> getShoesByUser(UUID userUUID) {
        Optional<User> userOptional = userRepository.findById(userUUID);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found with UUID: " + userUUID);
        }
        
        List<Shoe> shoes = shoeRepository.findByUser(userOptional.get());
        return shoes.stream()
            .map(shoe -> {
                ShoeDTO dto = new ShoeDTO();
                dto.setShoeId(shoe.getShoeId());
                dto.setBrand(shoe.getBrand());
                dto.setModel(shoe.getModel());
                dto.setSize(shoe.getSize());
                dto.setTotalMileage(shoe.getTotalMileage());
                dto.setPrice(shoe.getPrice());
                dto.setUserId(shoe.getUser().getUserId());
                return dto;
            })
            .collect(Collectors.toList());
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
}
