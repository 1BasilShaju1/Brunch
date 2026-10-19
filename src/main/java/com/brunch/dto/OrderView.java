package com.brunch.dto;

import com.brunch.model.Order;
import com.brunch.model.OrderItem;
import com.brunch.model.RestaurantProfile;
import com.brunch.model.User;

import java.util.ArrayList;
import java.util.List;

public class OrderView {
    private Order order;
    private User customer;
    private RestaurantProfile restaurant;
    private List<OrderItem> items = new ArrayList<>();

    public OrderView() {
    }

    public OrderView(Order order, User customer, RestaurantProfile restaurant, List<OrderItem> items) {
        this.order = order;
        this.customer = customer;
        this.restaurant = restaurant;
        this.items = items != null ? items : new ArrayList<>();
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public RestaurantProfile getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantProfile restaurant) {
        this.restaurant = restaurant;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    // Helper delegation methods for Thymeleaf convenience
    public Long getId() {
        return order != null ? order.getId() : null;
    }

    public Double getTotalAmount() {
        return order != null ? order.getTotalAmount() : 0.0;
    }

    public Double getDeliveryFee() {
        return order != null ? order.getDeliveryFee() : 0.0;
    }

    public Double getFinalAmount() {
        return order != null ? order.getFinalAmount() : 0.0;
    }

    public String getOrderType() {
        return order != null ? order.getOrderType() : "";
    }

    public String getPaymentMethod() {
        return order != null ? order.getPaymentMethod() : "";
    }

    public String getDeliveryAddress() {
        return order != null ? order.getDeliveryAddress() : "";
    }

    public String getOrderStatus() {
        return order != null ? order.getOrderStatus() : "";
    }

    public java.time.LocalDateTime getOrderDate() {
        return order != null ? order.getOrderDate() : null;
    }
}
