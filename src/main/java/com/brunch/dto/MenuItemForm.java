package com.brunch.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class MenuItemForm {

    private Long id;

    @NotBlank(message = "Item name is required")
    private String name;

    @NotBlank(message = "Category is required")
    @Pattern(regexp = "^(Breakfast|Main Course|Snacks|Drinks|Desserts)$", 
             message = "Category must be Breakfast, Main Course, Snacks, Drinks, or Desserts")
    private String category;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than zero")
    private Double price;

    @NotBlank(message = "Availability status is required")
    @Pattern(regexp = "^(Available|Unavailable)$", message = "Availability must be Available or Unavailable")
    private String availability;

    public MenuItemForm() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
