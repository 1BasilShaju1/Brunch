package com.brunch;

import java.io.Serializable;

// One line in the cart: a food item and how many the customer wants.
// (Serializable lets the web session store it safely.)
public class CartItem implements Serializable {

    private int foodItemId;
    private String name;
    private double price;
    private int quantity;

    public CartItem(int foodItemId, String name, double price, int quantity) {
        this.foodItemId = foodItemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public int getFoodItemId() {
        return foodItemId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // price x quantity for this line
    public double getLineTotal() {
        return price * quantity;
    }
}
