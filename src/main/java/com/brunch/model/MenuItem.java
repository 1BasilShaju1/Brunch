package com.brunch.model;

public class MenuItem {
    private Long id;
    private Long restaurantId;
    private String name;
    private String category; // Breakfast, Main Course, Snacks, Drinks, Desserts
    private String description;
    private Double price;
    private String availability; // Available, Unavailable

    public MenuItem() {
    }

    public MenuItem(Long id, Long restaurantId, String name, String category, String description, Double price, String availability) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.availability = availability != null ? availability : "Available";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
