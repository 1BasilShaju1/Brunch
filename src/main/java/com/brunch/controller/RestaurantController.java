package com.brunch.controller;

import com.brunch.dto.MenuItemForm;
import com.brunch.dto.RestaurantProfileForm;
import com.brunch.model.MenuItem;
import com.brunch.model.RestaurantProfile;
import com.brunch.model.User;
import com.brunch.service.MenuService;
import com.brunch.service.OrderService;
import com.brunch.service.RestaurantService;
import com.brunch.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/restaurant")
public class RestaurantController {

    private final UserService userService;
    private final RestaurantService restaurantService;
    private final MenuService menuService;
    private final OrderService orderService;

    public RestaurantController(UserService userService, RestaurantService restaurantService, MenuService menuService, OrderService orderService) {
        this.userService = userService;
        this.restaurantService = restaurantService;
        this.menuService = menuService;
        this.orderService = orderService;
    }

    private RestaurantProfile getAuthenticatedRestaurantProfile() {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            throw new SecurityException("User not authenticated");
        }
        return restaurantService.getProfileByUserId(user.getId())
                .orElseGet(() -> {
                    RestaurantProfile p = new RestaurantProfile();
                    p.setUserId(user.getId());
                    p.setRestaurantName(user.getFullName() + "'s Restaurant");
                    p.setCity("Bangalore");
                    p.setAddress("Main Street");
                    p.setPhone(user.getPhone());
                    p.setDescription("Freshly prepared quality meals.");
                    p.setOpeningTime("08:00 AM");
                    p.setClosingTime("11:00 PM");
                    return restaurantService.saveOrUpdateProfile(user.getId(), new RestaurantProfileForm());
                });
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User user = userService.getAuthenticatedUser();
        RestaurantProfile restaurant = getAuthenticatedRestaurantProfile();

        model.addAttribute("user", user);
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("totalMenuItems", menuService.countMenuItemsByRestaurant(restaurant.getId()));
        model.addAttribute("totalOrders", orderService.countTotalOrdersByRestaurant(restaurant.getId()));
        model.addAttribute("pendingOrders", orderService.countPendingOrdersByRestaurant(restaurant.getId()));
        model.addAttribute("revenue", orderService.calculateRestaurantRevenue(restaurant.getId()));

        return "restaurant-dashboard";
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        RestaurantProfile profile = getAuthenticatedRestaurantProfile();
        RestaurantProfileForm form = new RestaurantProfileForm();
        form.setRestaurantName(profile.getRestaurantName());
        form.setDescription(profile.getDescription());
        form.setPhone(profile.getPhone());
        form.setAddress(profile.getAddress());
        form.setCity(profile.getCity());
        form.setOpeningTime(profile.getOpeningTime());
        form.setClosingTime(profile.getClosingTime());

        model.addAttribute("profileForm", form);
        model.addAttribute("restaurant", profile);
        return "restaurant-profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileForm") RestaurantProfileForm form,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        RestaurantProfile profile = getAuthenticatedRestaurantProfile();
        if (bindingResult.hasErrors()) {
            model.addAttribute("restaurant", profile);
            return "restaurant-profile";
        }

        restaurantService.saveOrUpdateProfile(profile.getUserId(), form);
        redirectAttributes.addFlashAttribute("successMessage", "Restaurant profile updated successfully!");
        return "redirect:/restaurant/profile";
    }

    @GetMapping("/menu")
    public String viewMenuManagement(Model model) {
        RestaurantProfile restaurant = getAuthenticatedRestaurantProfile();
        List<MenuItem> items = menuService.getItemsByRestaurantId(restaurant.getId());
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("menuItems", items);
        if (!model.containsAttribute("menuItemForm")) {
            MenuItemForm form = new MenuItemForm();
            form.setAvailability("Available");
            model.addAttribute("menuItemForm", form);
        }
        return "restaurant-menu";
    }

    @PostMapping("/menu/add")
    public String addMenuItem(@Valid @ModelAttribute("menuItemForm") MenuItemForm form,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        RestaurantProfile restaurant = getAuthenticatedRestaurantProfile();
        if (bindingResult.hasErrors()) {
            model.addAttribute("restaurant", restaurant);
            model.addAttribute("menuItems", menuService.getItemsByRestaurantId(restaurant.getId()));
            model.addAttribute("showAddModal", true);
            return "restaurant-menu";
        }

        menuService.createMenuItem(restaurant.getId(), form);
        redirectAttributes.addFlashAttribute("successMessage", "Menu item added successfully!");
        return "redirect:/restaurant/menu";
    }

    @GetMapping("/menu/edit/{id}")
    public String showEditMenuItemForm(@PathVariable Long id, Model model) {
        RestaurantProfile restaurant = getAuthenticatedRestaurantProfile();
        MenuItem item = menuService.getMenuItemById(id);

        if (!restaurant.getId().equals(item.getRestaurantId())) {
            return "redirect:/restaurant/menu";
        }

        MenuItemForm form = new MenuItemForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setCategory(item.getCategory());
        form.setDescription(item.getDescription());
        form.setPrice(item.getPrice());
        form.setAvailability(item.getAvailability());

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("menuItemForm", form);
        model.addAttribute("menuItems", menuService.getItemsByRestaurantId(restaurant.getId()));
        model.addAttribute("editing", true);
        return "restaurant-menu";
    }

    @PostMapping("/menu/edit/{id}")
    public String editMenuItem(@PathVariable Long id,
                               @Valid @ModelAttribute("menuItemForm") MenuItemForm form,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        RestaurantProfile restaurant = getAuthenticatedRestaurantProfile();
        MenuItem existing = menuService.getMenuItemById(id);
        if (!restaurant.getId().equals(existing.getRestaurantId())) {
            return "redirect:/restaurant/menu";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("restaurant", restaurant);
            model.addAttribute("menuItems", menuService.getItemsByRestaurantId(restaurant.getId()));
            model.addAttribute("editing", true);
            return "restaurant-menu";
        }

        menuService.updateMenuItem(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Menu item updated successfully!");
        return "redirect:/restaurant/menu";
    }

    @PostMapping("/menu/delete/{id}")
    public String deleteMenuItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        RestaurantProfile restaurant = getAuthenticatedRestaurantProfile();
        MenuItem existing = menuService.getMenuItemById(id);
        if (restaurant.getId().equals(existing.getRestaurantId())) {
            menuService.deleteMenuItem(id);
            redirectAttributes.addFlashAttribute("successMessage", "Menu item deleted successfully!");
        }
        return "redirect:/restaurant/menu";
    }

    @PostMapping("/menu/availability/{id}")
    public String toggleAvailability(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        RestaurantProfile restaurant = getAuthenticatedRestaurantProfile();
        MenuItem existing = menuService.getMenuItemById(id);
        if (restaurant.getId().equals(existing.getRestaurantId())) {
            menuService.toggleAvailability(id);
            redirectAttributes.addFlashAttribute("successMessage", "Menu item availability updated!");
        }
        return "redirect:/restaurant/menu";
    }

    @GetMapping("/orders")
    public String viewOrders(Model model) {
        RestaurantProfile restaurant = getAuthenticatedRestaurantProfile();
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("orders", orderService.getRestaurantOrders(restaurant.getId()));
        return "restaurant-orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam("status") String status,
                                    RedirectAttributes redirectAttributes) {
        RestaurantProfile restaurant = getAuthenticatedRestaurantProfile();
        try {
            orderService.updateOrderStatus(id, status, restaurant.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Order #" + id + " status updated to " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/restaurant/orders";
    }
}
