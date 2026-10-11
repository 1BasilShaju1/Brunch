package com.brunch;

import java.util.List;

// An order placed by a customer.
// status is one of: PLACED, PREPARING, OUT_FOR_DELIVERY, DELIVERED
public class Order {

    private int id;
    private String customerName;
    private String phone;
    private String address;
    private List<CartItem> items;
    private double totalAmount;
    private String status;

    public Order(int id, String customerName, String phone, String address,
                 List<CartItem> items, double totalAmount, String status) {
        this.id = id;
        this.customerName = customerName;
        this.phone = phone;
        this.address = address;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
