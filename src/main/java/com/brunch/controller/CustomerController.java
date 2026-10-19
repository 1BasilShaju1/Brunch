package com.brunch.controller;

import com.brunch.dto.OrderView;
import com.brunch.model.User;
import com.brunch.service.OrderService;
import com.brunch.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final UserService userService;
    private final OrderService orderService;

    public CustomerController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User customer = userService.getAuthenticatedUser();
        if (customer == null) {
            return "redirect:/login";
        }
        List<OrderView> recentOrders = orderService.getCustomerOrders(customer);
        if (recentOrders.size() > 5) {
            recentOrders = recentOrders.subList(0, 5);
        }
        model.addAttribute("customer", customer);
        model.addAttribute("recentOrders", recentOrders);
        return "customer-dashboard";
    }
}
