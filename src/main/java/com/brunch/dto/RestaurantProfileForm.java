package com.brunch.dto;

import jakarta.validation.constraints.NotBlank;

public class RestaurantProfileForm {

    @NotBlank(message = "Restaurant name cannot be empty")
    private String restaurantName;

    private String description;

    @NotBlank(message = "Phone number cannot be empty")
    private String phone;

    @NotBlank(message = "Address cannot be empty")
    private String address;

    @NotBlank(message = "City cannot be empty")
    private String city;

    private String openingTime;

    private String closingTime;

    public RestaurantProfileForm() {
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
