package com.example.RunAgainstTheWind.domain.shoe.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public List<ShoeDTO> getShoesByUserID(UUID userId) { 
        v.validateUserExists(userId);
        return shoeRepository.getShoesByUserId(userId);
    }

    @Transactional
    public ShoeDTO createShoe(UUID userId, ShoeDTO shoeDTO) {
        User user = v.validateUserExistsAndReturn(userId);

        Shoe shoe = new Shoe(shoeDTO.getModel(), 
            shoeDTO.getBrand(), 
            shoeDTO.getColor(), 
            shoeDTO.getTotalMileage(), 
            LocalDate.now(), 
            user
        );
        Shoe savedShoe = shoeRepository.save(shoe);

        shoeDTO.setShoeId(savedShoe.getShoeId());
        shoeDTO.setDate(savedShoe.getDate());
        shoeDTO.setUserId(user.getUserId());
        return shoeDTO;
    }

    @Transactional
    public void deleteShoe(Long shoeId) {
        if (!shoeRepository.existsById(shoeId)) throw new RuntimeException("Shoe not found with id: " + shoeId);
        shoeRepository.deleteById(shoeId);
    }

    @Transactional
    public ShoeDTO updateShoe(Long shoeId, ShoeDTO shoeDTO) {
        Shoe shoe = shoeRepository.findById(shoeId).orElseThrow(() -> new RuntimeException("Shoe not found with id: " + shoeId));

        if (shoeDTO.getModel() != null) shoe.setModel(shoeDTO.getModel());
        if (shoeDTO.getBrand() != null) shoe.setBrand(shoeDTO.getBrand());
        if (shoeDTO.getColor() != null) shoe.setColor(shoeDTO.getColor());
        if (shoeDTO.getTotalMileage() != null) shoe.setTotalMileage(shoeDTO.getTotalMileage());
        Shoe updatedShoe = shoeRepository.save(shoe);

        return new ShoeDTO(
            updatedShoe.getShoeId(),
            updatedShoe.getModel(),
            updatedShoe.getBrand(),
            updatedShoe.getColor(),
            updatedShoe.getTotalMileage(),
            updatedShoe.getDate(),
            updatedShoe.getUser().getUserId()
        );
    }
}
