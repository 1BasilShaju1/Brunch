package com.brunch.service;

import com.brunch.entity.Cart;
import com.brunch.entity.CartItem;
import com.brunch.entity.MenuItem;
import com.brunch.entity.User;
import com.brunch.repository.CartItemRepository;
import com.brunch.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuService menuService;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, MenuService menuService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.menuService = menuService;
    }

    public Cart getOrCreateCart(User customer) {
        return cartRepository.findByCustomer(customer)
                .orElseGet(() -> cartRepository.save(new Cart(customer)));
    }

    @Transactional
    public void addItemToCart(User customer, Long menuItemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        MenuItem menuItem = menuService.getMenuItemById(menuItemId);
        if (!"Available".equalsIgnoreCase(menuItem.getAvailability())) {
            throw new IllegalStateException("Sorry, this item is currently unavailable");
        }

        Cart cart = getOrCreateCart(customer);
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndMenuItem(cart, menuItem);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem(cart, menuItem, quantity);
            cartItemRepository.save(newItem);
        }
    }

    @Transactional
    public void updateCartItemQuantity(User customer, Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!cartItem.getCart().getCustomer().getId().equals(customer.getId())) {
            throw new SecurityException("Unauthorized cart modification");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }
    }

    @Transactional
    public void removeCartItem(User customer, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        if (!cartItem.getCart().getCustomer().getId().equals(customer.getId())) {
            throw new SecurityException("Unauthorized cart modification");
        }
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void clearCart(Cart cart) {
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public Double calculateCartTotal(Cart cart) {
        return cart.getItems().stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }
}
