package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.entities.MenuItem;
import com.example.CGI_Restaurant.domain.entities.Restaurant;

import java.math.BigDecimal;
import java.util.List;

/**
 * Integration with TheMealDB public API (https://www.themealdb.com/api.php).
 */
public interface TheMealDBService {

    List<TheMealDBCategoryDto> getCategories();

    List<TheMealDBMealSummaryDto> getMealsByCategory(String category);

    TheMealDBMealDetailDto getMealById(String id);

    MenuItem importMealAsMenuItem(Restaurant restaurant, String themealdbMealId, BigDecimal priceEur);

    record TheMealDBCategoryDto(String idCategory, String strCategory, String strCategoryThumb, String strCategoryDescription) {}

    record TheMealDBMealSummaryDto(String idMeal, String strMeal, String strMealThumb) {}

    record TheMealDBMealDetailDto(String idMeal, String strMeal, String strCategory, String strInstructions, String strMealThumb) {}
}
