package com.brunch.model;

public class CartItem {
    private Long id;
    private Long cartId;
    private Long menuItemId;
    private Integer quantity;

    public CartItem() {
    }

    public CartItem(Long id, Long cartId, Long menuItemId, Integer quantity) {
        this.id = id;
        this.cartId = cartId;
        this.menuItemId = menuItemId;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
