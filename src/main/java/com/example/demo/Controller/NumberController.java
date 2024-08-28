package com.example.demo.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.NumberService;

import java.util.Map;

@RestController
public class NumberController {

    private final NumberService numberService;

    public NumberController(NumberService numberService) {
        this.numberService = numberService;
    }

    @GetMapping("/numbers/{type}")
    public Map<String, Object> getNumbers(@PathVariable String type) {
        return numberService.processNumbers(type);
    }
}