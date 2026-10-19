package com.brunch.controller;

import com.brunch.service.MenuService;
import com.brunch.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final MenuService menuService;
    private final RestaurantService restaurantService;

    public HomeController(MenuService menuService, RestaurantService restaurantService) {
        this.menuService = menuService;
        this.restaurantService = restaurantService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        model.addAttribute("popularItems", menuService.getAllAvailableItems());
        return "index";
    }
}
