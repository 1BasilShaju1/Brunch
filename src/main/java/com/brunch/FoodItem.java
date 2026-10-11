package com.brunch;

// One dish on a restaurant's menu.
public class FoodItem {

    private int id;
    private int restaurantId; // which restaurant sells this item
    private String name;
    private double price;

    public FoodItem(int id, int restaurantId, String name, double price) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}
