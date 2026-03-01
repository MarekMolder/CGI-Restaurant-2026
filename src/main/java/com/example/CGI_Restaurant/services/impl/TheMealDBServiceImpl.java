package com.example.CGI_Restaurant.services.impl;

import com.example.CGI_Restaurant.domain.entities.MenuItem;
import com.example.CGI_Restaurant.domain.entities.Restaurant;
import com.example.CGI_Restaurant.repositories.MenuItemRepository;
import com.example.CGI_Restaurant.services.TheMealDBService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Fetches categories and meals from TheMealDB API; can import a meal as a menu item for a restaurant.
 * @author AI (assisted)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TheMealDBServiceImpl implements TheMealDBService {

    private static final String CATEGORIES = "categories.php";
    private static final String FILTER = "filter.php";
    private static final String LOOKUP = "lookup.php";

    private final RestTemplate restTemplate;
    private final MenuItemRepository menuItemRepository;

    @Value("${app.themealdb.base-url:https://www.themealdb.com/api/json/v1/1}")
    private String baseUrl;

    @Override
    public List<TheMealDBCategoryDto> getCategories() {
        try {
            CategoriesResponse response = restTemplate.getForObject(baseUrl + "/" + CATEGORIES, CategoriesResponse.class);
            return response != null && response.categories != null ? response.categories : Collections.emptyList();
        } catch (Exception e) {
            log.warn("Failed to fetch TheMealDB categories: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<TheMealDBMealSummaryDto> getMealsByCategory(String category) {
        if (category == null || category.isBlank()) {
            return Collections.emptyList();
        }
        try {
            String url = baseUrl + "/" + FILTER + "?c=" + category.trim();
            MealsResponse response = restTemplate.getForObject(url, MealsResponse.class);
            if (response == null || response.meals == null) {
                return Collections.emptyList();
            }
            return response.meals.stream()
                    .map(m -> new TheMealDBMealSummaryDto(m.idMeal(), m.strMeal(), m.strMealThumb()))
                    .toList();
        } catch (Exception e) {
            log.warn("Failed to fetch TheMealDB meals for category {}: {}", category, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public TheMealDBMealDetailDto getMealById(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        try {
            String url = baseUrl + "/" + LOOKUP + "?i=" + id.trim();
            MealsResponse response = restTemplate.getForObject(url, MealsResponse.class);
            if (response != null && response.meals != null && !response.meals.isEmpty()) {
                MealDetailRaw m = response.meals.get(0);
                return new TheMealDBMealDetailDto(m.idMeal(), m.strMeal(), m.strCategory(), m.strInstructions(), m.strMealThumb());
            }
        } catch (Exception e) {
            log.warn("Failed to fetch TheMealDB meal by id {}: {}", id, e.getMessage());
        }
        return null;
    }

    @Override
    public MenuItem importMealAsMenuItem(Restaurant restaurant, String themealdbMealId, BigDecimal priceEur) {
        TheMealDBMealDetailDto detail = getMealById(themealdbMealId);
        if (detail == null) {
            throw new IllegalArgumentException("TheMealDB meal not found: " + themealdbMealId);
        }
        MenuItem item = new MenuItem();
        item.setName(detail.strMeal());
        item.setDescription(detail.strInstructions());
        item.setPriceEur(priceEur != null ? priceEur : BigDecimal.ZERO);
        item.setCategory(detail.strCategory());
        item.setImageUrl(detail.strMealThumb());
        item.setThemealdbId(detail.idMeal());
        item.setRestaurant(restaurant);
        LocalDateTime now = LocalDateTime.now();
        item.setCreatedAt(now);
        item.setUpdatedAt(now);
        return menuItemRepository.save(item);
    }

    private record CategoriesResponse(List<TheMealDBCategoryDto> categories) {}

    private record MealsResponse(List<MealDetailRaw> meals) {}

    private record MealDetailRaw(String idMeal, String strMeal, String strCategory, String strInstructions, String strMealThumb) {}
}
