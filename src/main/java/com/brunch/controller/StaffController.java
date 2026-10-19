package com.brunch.controller;

import com.brunch.dto.MenuItemForm;
import com.brunch.entity.MenuItem;
import com.brunch.entity.User;
import com.brunch.service.MenuService;
import com.brunch.service.OrderService;
import com.brunch.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final UserService userService;
    private final MenuService menuService;
    private final OrderService orderService;

    public StaffController(UserService userService, MenuService menuService, OrderService orderService) {
        this.userService = userService;
        this.menuService = menuService;
        this.orderService = orderService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User staff = userService.getAuthenticatedUser();
        model.addAttribute("staff", staff);
        model.addAttribute("totalMenuItems", menuService.countMenuItems());
        model.addAttribute("totalOrders", orderService.countTotalOrders());
        model.addAttribute("pendingOrders", orderService.countPendingOrders());
        return "staff-dashboard";
    }

    @GetMapping("/menu")
    public String viewMenuManagement(Model model) {
        model.addAttribute("menuItems", menuService.getAllMenuItems());
        return "staff-menu";
    }

    @GetMapping("/menu/add")
    public String showAddMenuItemForm(Model model) {
        MenuItemForm form = new MenuItemForm();
        form.setAvailability("Available");
        model.addAttribute("menuItemForm", form);
        return "staff-menu";
    }

    @PostMapping("/menu/add")
    public String addMenuItem(@Valid @ModelAttribute("menuItemForm") MenuItemForm form,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("menuItems", menuService.getAllMenuItems());
            model.addAttribute("showAddModal", true);
            return "staff-menu";
        }
        menuService.createMenuItem(form);
        redirectAttributes.addFlashAttribute("successMessage", "Menu item added successfully!");
        return "redirect:/staff/menu";
    }

    @GetMapping("/menu/edit/{id}")
    public String showEditMenuItemForm(@PathVariable Long id, Model model) {
        MenuItem item = menuService.getMenuItemById(id);
        MenuItemForm form = new MenuItemForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setCategory(item.getCategory());
        form.setDescription(item.getDescription());
        form.setPrice(item.getPrice());
        form.setAvailability(item.getAvailability());

        model.addAttribute("menuItemForm", form);
        model.addAttribute("menuItems", menuService.getAllMenuItems());
        model.addAttribute("editing", true);
        return "staff-menu";
    }

    @PostMapping("/menu/edit/{id}")
    public String editMenuItem(@PathVariable Long id,
                               @Valid @ModelAttribute("menuItemForm") MenuItemForm form,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("menuItems", menuService.getAllMenuItems());
            model.addAttribute("editing", true);
            return "staff-menu";
        }
        menuService.updateMenuItem(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Menu item updated successfully!");
        return "redirect:/staff/menu";
    }

    @PostMapping("/menu/delete/{id}")
    public String deleteMenuItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        menuService.deleteMenuItem(id);
        redirectAttributes.addFlashAttribute("successMessage", "Menu item deleted successfully!");
        return "redirect:/staff/menu";
    }

    @PostMapping("/menu/availability/{id}")
    public String toggleAvailability(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        menuService.toggleAvailability(id);
        redirectAttributes.addFlashAttribute("successMessage", "Menu item status updated!");
        return "redirect:/staff/menu";
    }

    @GetMapping("/orders")
    public String viewOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "staff-orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam("status") String status,
                                    RedirectAttributes redirectAttributes) {
        orderService.updateOrderStatus(id, status);
        redirectAttributes.addFlashAttribute("successMessage", "Order #" + id + " updated to " + status);
        return "redirect:/staff/orders";
    }
}
