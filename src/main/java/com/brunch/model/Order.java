package com.brunch.model;

import java.time.LocalDateTime;

public class Order {
    private Long id;
    private Long customerId;
    private Long restaurantId;
    private Double totalAmount;
    private Double deliveryFee;
    private Double finalAmount;
    private String orderType; // Pickup, Delivery
    private String paymentMethod; // Cash on Delivery, UPI
    private String deliveryAddress;
    private String orderStatus; // PENDING, ACCEPTED, PREPARING, READY_FOR_PICKUP, OUT_FOR_DELIVERY, DELIVERED, CANCELLED
    private LocalDateTime orderDate;

    public Order() {
    }

    public Order(Long id, Long customerId, Long restaurantId, Double totalAmount, Double deliveryFee, Double finalAmount,
                 String orderType, String paymentMethod, String deliveryAddress, String orderStatus, LocalDateTime orderDate) {
        this.id = id;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.totalAmount = totalAmount;
        this.deliveryFee = deliveryFee;
        this.finalAmount = finalAmount;
        this.orderType = orderType;
        this.paymentMethod = paymentMethod;
        this.deliveryAddress = deliveryAddress;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate != null ? orderDate : LocalDateTime.now();
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

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(Double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
}
