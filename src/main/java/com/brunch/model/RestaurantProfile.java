package com.brunch.model;

public class RestaurantProfile {
    private Long id;
    private Long userId;
    private String restaurantName;
    private String description;
    private String phone;
    private String address;
    private String city;
    private String openingTime;
    private String closingTime;

    public RestaurantProfile() {
    }

    public RestaurantProfile(Long id, Long userId, String restaurantName, String description, String phone, String address, String city, String openingTime, String closingTime) {
        this.id = id;
        this.userId = userId;
        this.restaurantName = restaurantName;
        this.description = description;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }
}
