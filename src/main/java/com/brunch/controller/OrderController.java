package com.brunch.controller;

import com.brunch.dto.CheckoutForm;
import com.brunch.entity.Cart;
import com.brunch.entity.Order;
import com.brunch.entity.User;
import com.brunch.service.CartService;
import com.brunch.service.OrderService;
import com.brunch.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;

    public OrderController(OrderService orderService, CartService cartService, UserService userService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/checkout")
    public String showCheckoutPage(Model model) {
        User customer = userService.getAuthenticatedUser();
        Cart cart = cartService.getOrCreateCart(customer);
        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        double subtotal = cartService.calculateCartTotal(cart);
        model.addAttribute("cart", cart);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("checkoutForm", new CheckoutForm());
        return "checkout";
    }

    @PostMapping("/checkout")
    public String processOrder(@Valid @ModelAttribute("checkoutForm") CheckoutForm form,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        User customer = userService.getAuthenticatedUser();
        Cart cart = cartService.getOrCreateCart(customer);

        if (bindingResult.hasErrors()) {
            model.addAttribute("cart", cart);
            model.addAttribute("subtotal", cartService.calculateCartTotal(cart));
            return "checkout";
        }

        try {
            Order order = orderService.placeOrder(customer, form);
            return "redirect:/orders/" + order.getId() + "?success=true";
        } catch (Exception e) {
            model.addAttribute("cart", cart);
            model.addAttribute("subtotal", cartService.calculateCartTotal(cart));
            model.addAttribute("errorMessage", e.getMessage());
            return "checkout";
        }
    }

    @GetMapping("/orders")
    public String viewOrderHistory(Model model) {
        User customer = userService.getAuthenticatedUser();
        model.addAttribute("orders", orderService.getCustomerOrders(customer));
        return "customer-orders";
    }

    @GetMapping("/orders/{id}")
    public String viewOrderConfirmation(@PathVariable Long id,
                                        @RequestParam(value = "success", defaultValue = "false") boolean success,
                                        Model model) {
        User customer = userService.getAuthenticatedUser();
        Order order = orderService.getOrderById(id);

        if (!order.getCustomer().getId().equals(customer.getId())) {
            return "redirect:/error";
        }

        model.addAttribute("order", order);
        return "order-success";
    }

    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User customer = userService.getAuthenticatedUser();
        try {
            orderService.cancelPendingOrder(id, customer);
            redirectAttributes.addFlashAttribute("successMessage", "Order #" + id + " has been cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/orders";
    }
}
