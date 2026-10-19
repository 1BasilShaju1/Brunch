package com.brunch.service;

import com.brunch.dto.CartItemView;
import com.brunch.dto.CartView;
import com.brunch.model.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final FileStorageService fileStorageService;
    private final MenuService menuService;
    private final RestaurantService restaurantService;

    public CartService(FileStorageService fileStorageService, MenuService menuService, RestaurantService restaurantService) {
        this.fileStorageService = fileStorageService;
        this.menuService = menuService;
        this.restaurantService = restaurantService;
    }

    public Cart getOrCreateCart(User customer) {
        if (customer == null || customer.getId() == null) {
            throw new IllegalArgumentException("Customer must not be null");
        }
        return fileStorageService.findCartByCustomerId(customer.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomerId(customer.getId());
                    return fileStorageService.saveCart(newCart);
                });
    }

    public CartView getCartView(User customer) {
        Cart cart = getOrCreateCart(customer);
        List<CartItem> cartItems = fileStorageService.findCartItemsByCartId(cart.getId());

        CartView cartView = new CartView();
        cartView.setId(cart.getId());
        cartView.setCustomerId(customer.getId());

        List<CartItemView> itemViews = new ArrayList<>();
        double subtotal = 0.0;
        Long restaurantId = null;

        for (CartItem ci : cartItems) {
            Optional<MenuItem> itemOpt = fileStorageService.findMenuItemById(ci.getMenuItemId());
            if (itemOpt.isPresent()) {
                MenuItem item = itemOpt.get();
                CartItemView civ = new CartItemView(ci.getId(), item, ci.getQuantity());
                itemViews.add(civ);
                subtotal += civ.getSubtotal();
                if (restaurantId == null && item.getRestaurantId() != null) {
                    restaurantId = item.getRestaurantId();
                }
            }
        }

        cartView.setItems(itemViews);
        cartView.setSubtotal(subtotal);
        cartView.setRestaurantId(restaurantId);

        if (restaurantId != null) {
            restaurantService.getProfileById(restaurantId)
                    .ifPresent(r -> cartView.setRestaurantName(r.getRestaurantName()));
        }

        double deliveryFee = (subtotal > 0) ? 30.0 : 0.0;
        cartView.setDeliveryFee(deliveryFee);
        cartView.setTotal(subtotal > 0 ? (subtotal + deliveryFee) : 0.0);

        return cartView;
    }

    public void addItemToCart(User customer, Long menuItemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        MenuItem menuItem = menuService.getMenuItemById(menuItemId);
        if (!"Available".equalsIgnoreCase(menuItem.getAvailability())) {
            throw new IllegalStateException("Sorry, this item is currently unavailable");
        }

        Cart cart = getOrCreateCart(customer);
        List<CartItem> currentItems = fileStorageService.findCartItemsByCartId(cart.getId());

        // Check if items from a different restaurant already exist in the cart
        for (CartItem ci : currentItems) {
            Optional<MenuItem> existingItemOpt = fileStorageService.findMenuItemById(ci.getMenuItemId());
            if (existingItemOpt.isPresent()) {
                MenuItem existingItem = existingItemOpt.get();
                if (existingItem.getRestaurantId() != null && menuItem.getRestaurantId() != null
                        && !existingItem.getRestaurantId().equals(menuItem.getRestaurantId())) {
                    throw new IllegalStateException("Your cart contains items from another restaurant. Please clear your cart first before ordering from a new restaurant.");
                }
            }
        }

        Optional<CartItem> existingCartItem = fileStorageService.findCartItemByCartIdAndMenuItemId(cart.getId(), menuItemId);
        if (existingCartItem.isPresent()) {
            CartItem item = existingCartItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            fileStorageService.saveCartItem(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCartId(cart.getId());
            newItem.setMenuItemId(menuItemId);
            newItem.setQuantity(quantity);
            fileStorageService.saveCartItem(newItem);
        }
    }

    public void updateCartItemQuantity(User customer, Long cartItemId, int quantity) {
        CartItem cartItem = fileStorageService.findCartItemById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        Cart cart = fileStorageService.findCartById(cartItem.getCartId())
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        if (!customer.getId().equals(cart.getCustomerId())) {
            throw new SecurityException("Unauthorized cart modification");
        }

        if (quantity <= 0) {
            fileStorageService.deleteCartItem(cartItemId);
        } else {
            cartItem.setQuantity(quantity);
            fileStorageService.saveCartItem(cartItem);
        }
    }

    public void removeCartItem(User customer, Long cartItemId) {
        CartItem cartItem = fileStorageService.findCartItemById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        Cart cart = fileStorageService.findCartById(cartItem.getCartId())
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        if (!customer.getId().equals(cart.getCustomerId())) {
            throw new SecurityException("Unauthorized cart modification");
        }

        fileStorageService.deleteCartItem(cartItemId);
    }

    public void clearCart(User customer) {
        Cart cart = getOrCreateCart(customer);
        fileStorageService.deleteCartItemsByCartId(cart.getId());
    }
}
