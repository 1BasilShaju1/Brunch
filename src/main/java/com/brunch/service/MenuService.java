package com.brunch.service;

import com.brunch.dto.MenuItemForm;
import com.brunch.model.MenuItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final FileStorageService fileStorageService;

    public MenuService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public List<MenuItem> getAllAvailableItems() {
        return fileStorageService.getMenuItems().stream()
                .filter(m -> "Available".equalsIgnoreCase(m.getAvailability()))
                .collect(Collectors.toList());
    }

    public List<MenuItem> getAvailableItemsByCategory(String category) {
        return fileStorageService.getMenuItems().stream()
                .filter(m -> "Available".equalsIgnoreCase(m.getAvailability()))
                .filter(m -> category.equalsIgnoreCase(m.getCategory()))
                .collect(Collectors.toList());
    }

    public List<MenuItem> searchAvailableItems(String query) {
        if (query == null || query.isBlank()) {
            return getAllAvailableItems();
        }
        String q = query.trim().toLowerCase();
        return fileStorageService.getMenuItems().stream()
                .filter(m -> "Available".equalsIgnoreCase(m.getAvailability()))
                .filter(m -> (m.getName() != null && m.getName().toLowerCase().contains(q))
                        || (m.getDescription() != null && m.getDescription().toLowerCase().contains(q))
                        || (m.getCategory() != null && m.getCategory().toLowerCase().contains(q)))
                .collect(Collectors.toList());
    }

    public List<MenuItem> getAllMenuItems() {
        return fileStorageService.getMenuItems();
    }

    public List<MenuItem> getItemsByRestaurantId(Long restaurantId) {
        return fileStorageService.findMenuItemsByRestaurantId(restaurantId);
    }

    public List<MenuItem> getAvailableItemsByRestaurantId(Long restaurantId) {
        return fileStorageService.findMenuItemsByRestaurantId(restaurantId).stream()
                .filter(m -> "Available".equalsIgnoreCase(m.getAvailability()))
                .collect(Collectors.toList());
    }

    public MenuItem getMenuItemById(Long id) {
        return fileStorageService.findMenuItemById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found with ID: " + id));
    }

    public MenuItem createMenuItem(Long restaurantId, MenuItemForm form) {
        MenuItem item = new MenuItem();
        item.setRestaurantId(restaurantId);
        item.setName(form.getName().trim());
        item.setCategory(form.getCategory().trim());
        item.setDescription(form.getDescription().trim());
        item.setPrice(form.getPrice());
        item.setAvailability(form.getAvailability() != null ? form.getAvailability().trim() : "Available");
        return fileStorageService.saveMenuItem(item);
    }

    public MenuItem updateMenuItem(Long id, MenuItemForm form) {
        MenuItem item = getMenuItemById(id);
        item.setName(form.getName().trim());
        item.setCategory(form.getCategory().trim());
        item.setDescription(form.getDescription().trim());
        item.setPrice(form.getPrice());
        item.setAvailability(form.getAvailability() != null ? form.getAvailability().trim() : "Available");
        return fileStorageService.saveMenuItem(item);
    }

    public void toggleAvailability(Long id) {
        MenuItem item = getMenuItemById(id);
        if ("Available".equalsIgnoreCase(item.getAvailability())) {
            item.setAvailability("Unavailable");
        } else {
            item.setAvailability("Available");
        }
        fileStorageService.saveMenuItem(item);
    }

    public void deleteMenuItem(Long id) {
        fileStorageService.deleteMenuItem(id);
    }

    public long countMenuItemsByRestaurant(Long restaurantId) {
        return fileStorageService.findMenuItemsByRestaurantId(restaurantId).size();
    }
}
