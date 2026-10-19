package com.brunch.dto;

import java.util.ArrayList;
import java.util.List;

public class CartView {
    private Long id;
    private Long customerId;
    private Long restaurantId;
    private String restaurantName;
    private List<CartItemView> items = new ArrayList<>();
    private Double subtotal = 0.0;
    private Double deliveryFee = 30.0;
    private Double total = 0.0;

    public CartView() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public List<CartItemView> getItems() {
        return items;
    }

    public void setItems(List<CartItemView> items) {
        this.items = items;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}
