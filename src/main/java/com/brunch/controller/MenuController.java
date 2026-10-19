package com.brunch.controller;

import com.brunch.dto.CartItemForm;
import com.brunch.model.MenuItem;
import com.brunch.model.RestaurantProfile;
import com.brunch.service.MenuService;
import com.brunch.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MenuController {

    private final MenuService menuService;
    private final RestaurantService restaurantService;

    public MenuController(MenuService menuService, RestaurantService restaurantService) {
        this.menuService = menuService;
        this.restaurantService = restaurantService;
    }

    @GetMapping("/menu")
    public String viewMenu(Model model) {
        model.addAttribute("menuItems", menuService.getAllAvailableItems());
        model.addAttribute("selectedCategory", "All");
        return "menu";
    }

    @GetMapping("/menu/category/{category}")
    public String filterCategory(@PathVariable String category, Model model) {
        model.addAttribute("menuItems", menuService.getAvailableItemsByCategory(category));
        model.addAttribute("selectedCategory", category);
        return "menu";
    }

    @GetMapping("/menu/search")
    public String searchMenu(@RequestParam("query") String query, Model model) {
        model.addAttribute("menuItems", menuService.searchAvailableItems(query));
        model.addAttribute("selectedCategory", "Search Results");
        model.addAttribute("searchQuery", query);
        return "menu";
    }

    @GetMapping("/menu/{id}")
    public String viewItemDetails(@PathVariable Long id, Model model) {
        MenuItem item = menuService.getMenuItemById(id);
        RestaurantProfile restaurant = null;
        if (item.getRestaurantId() != null) {
            restaurant = restaurantService.getProfileById(item.getRestaurantId()).orElse(null);
        }
        model.addAttribute("item", item);
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("cartItemForm", new CartItemForm(1));
        return "menu-details";
    }

    @GetMapping("/restaurants")
    public String viewRestaurants(@RequestParam(value = "query", required = false) String query,
                                  @RequestParam(value = "city", required = false) String city,
                                  Model model) {
        List<RestaurantProfile> restaurants = restaurantService.searchRestaurants(query, city);
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("query", query);
        model.addAttribute("selectedCity", city != null && !city.isBlank() ? city : "All");
        return "restaurants";
    }

    @GetMapping("/restaurants/{id}")
    public String viewRestaurantDetails(@PathVariable Long id, Model model) {
        RestaurantProfile restaurant = restaurantService.getProfileById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found with ID: " + id));
        List<MenuItem> items = menuService.getAvailableItemsByRestaurantId(id);

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("menuItems", items);
        return "restaurant-details";
    }
}
