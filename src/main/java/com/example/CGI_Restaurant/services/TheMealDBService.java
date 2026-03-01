package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.entities.MenuItem;
import com.example.CGI_Restaurant.domain.entities.Restaurant;

import java.math.BigDecimal;
import java.util.List;

/**
 * Integration with TheMealDB public API (https://www.themealdb.com/api.php).
 */
public interface TheMealDBService {

    /** Returns all meal categories from TheMealDB API. */
    List<TheMealDBCategoryDto> getCategories();

    /** Returns meals in the given category. */
    List<TheMealDBMealSummaryDto> getMealsByCategory(String category);

    /** Returns a single meal detail by TheMealDB meal id. */
    TheMealDBMealDetailDto getMealById(String id);

    /** Imports a meal from TheMealDB as a new menu item for the restaurant. */
    MenuItem importMealAsMenuItem(Restaurant restaurant, String themealdbMealId, BigDecimal priceEur);

    record TheMealDBCategoryDto(String idCategory, String strCategory, String strCategoryThumb, String strCategoryDescription) {}

    record TheMealDBMealSummaryDto(String idMeal, String strMeal, String strMealThumb) {}

    record TheMealDBMealDetailDto(String idMeal, String strMeal, String strCategory, String strInstructions, String strMealThumb) {}
}
