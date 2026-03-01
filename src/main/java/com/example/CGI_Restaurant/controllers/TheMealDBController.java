package com.example.CGI_Restaurant.controllers;

import com.example.CGI_Restaurant.services.TheMealDBService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for TheMealDB integration: list categories and meals for importing into the restaurant menu. Read-only, public.
 */
@RestController
@RequestMapping(path = "/api/v1/themealdb")
@RequiredArgsConstructor
public class TheMealDBController {

    private final TheMealDBService theMealDBService;

    /** Returns all meal categories from TheMealDB. */
    @GetMapping("/categories")
    public ResponseEntity<List<TheMealDBService.TheMealDBCategoryDto>> getCategories() {
        return ResponseEntity.ok(theMealDBService.getCategories());
    }

    /** Returns meals in the given category. */
    @GetMapping("/meals")
    public ResponseEntity<List<TheMealDBService.TheMealDBMealSummaryDto>> getMealsByCategory(
            @RequestParam String category) {
        return ResponseEntity.ok(theMealDBService.getMealsByCategory(category));
    }

    /** Returns a single meal detail by TheMealDB id, or 404. */
    @GetMapping("/meals/{id}")
    public ResponseEntity<TheMealDBService.TheMealDBMealDetailDto> getMealById(@PathVariable String id) {
        TheMealDBService.TheMealDBMealDetailDto meal = theMealDBService.getMealById(id);
        return meal != null ? ResponseEntity.ok(meal) : ResponseEntity.notFound().build();
    }
}
