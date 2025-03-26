package com.example.RunAgainstTheWind.domain.example.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.RunAgainstTheWind.domain.example.model.Example;

// TEST: Just here for API testing
@RestController
public class ExampleController {
    private List<Example> students = new ArrayList<>(List.of(
            new Example(1, "John", 10),
            new Example(2, "Jane", 100)
    ));

    @GetMapping("/example")
    public List<Example> getStudents() {
        return students;
    }
}