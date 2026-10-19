package com.brunch.controller;

import com.brunch.service.MenuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final MenuService menuService;

    public HomeController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("popularItems", menuService.getAllAvailableItems());
        return "index";
    }
}
