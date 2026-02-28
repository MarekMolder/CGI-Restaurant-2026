package com.example.CGI_Restaurant.controllers;

import com.example.CGI_Restaurant.services.TheMealDBService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/themealdb")
@RequiredArgsConstructor
public class TheMealDBController {

    private final TheMealDBService theMealDBService;

    @GetMapping("/categories")
    public ResponseEntity<List<TheMealDBService.TheMealDBCategoryDto>> getCategories() {
        return ResponseEntity.ok(theMealDBService.getCategories());
    }

    @GetMapping("/meals")
    public ResponseEntity<List<TheMealDBService.TheMealDBMealSummaryDto>> getMealsByCategory(
            @RequestParam String category) {
        return ResponseEntity.ok(theMealDBService.getMealsByCategory(category));
    }

    @GetMapping("/meals/{id}")
    public ResponseEntity<TheMealDBService.TheMealDBMealDetailDto> getMealById(@PathVariable String id) {
        TheMealDBService.TheMealDBMealDetailDto meal = theMealDBService.getMealById(id);
        return meal != null ? ResponseEntity.ok(meal) : ResponseEntity.notFound().build();
    }
}
