package com.brunch.dto;

import com.brunch.model.MenuItem;

public class CartItemView {
    private Long id; // CartItem id
    private MenuItem menuItem;
    private Integer quantity;

    public CartItemView() {
    }

    public CartItemView(Long id, MenuItem menuItem, Integer quantity) {
        this.id = id;
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getSubtotal() {
        if (menuItem != null && menuItem.getPrice() != null && quantity != null) {
            return menuItem.getPrice() * quantity;
        }
        return 0.0;
    }
}
