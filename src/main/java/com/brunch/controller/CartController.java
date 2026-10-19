package com.brunch.controller;

import com.brunch.dto.CartItemForm;
import com.brunch.entity.Cart;
import com.brunch.entity.User;
import com.brunch.service.CartService;
import com.brunch.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public String viewCart(Model model) {
        User customer = userService.getAuthenticatedUser();
        Cart cart = cartService.getOrCreateCart(customer);
        double subtotal = cartService.calculateCartTotal(cart);

        model.addAttribute("cart", cart);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("deliveryFee", 30.0);
        model.addAttribute("total", subtotal > 0 ? (subtotal + 30.0) : 0.0);
        return "cart";
    }

    @PostMapping("/add/{menuItemId}")
    public String addToCart(@PathVariable Long menuItemId,
                            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                            RedirectAttributes redirectAttributes) {
        User customer = userService.getAuthenticatedUser();
        try {
            cartService.addItemToCart(customer, menuItemId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Item added to cart!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/update/{cartItemId}")
    public String updateQuantity(@PathVariable Long cartItemId,
                                 @Valid @ModelAttribute CartItemForm form,
                                 RedirectAttributes redirectAttributes) {
        User customer = userService.getAuthenticatedUser();
        try {
            cartService.updateCartItemQuantity(customer, cartItemId, form.getQuantity());
            redirectAttributes.addFlashAttribute("successMessage", "Cart updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove/{cartItemId}")
    public String removeItem(@PathVariable Long cartItemId, RedirectAttributes redirectAttributes) {
        User customer = userService.getAuthenticatedUser();
        try {
            cartService.removeCartItem(customer, cartItemId);
            redirectAttributes.addFlashAttribute("successMessage", "Item removed from cart!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/cart";
    }
}
