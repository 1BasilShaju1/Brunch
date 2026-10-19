package com.brunch.controller;

import com.brunch.dto.CartItemForm;
import com.brunch.entity.MenuItem;
import com.brunch.service.MenuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
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
        model.addAttribute("item", item);
        model.addAttribute("cartItemForm", new CartItemForm(1));
        return "menu-details";
    }
}
