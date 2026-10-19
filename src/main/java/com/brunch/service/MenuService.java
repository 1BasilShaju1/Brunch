package com.brunch.service;

import com.brunch.dto.MenuItemForm;
import com.brunch.entity.MenuItem;
import com.brunch.repository.MenuItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    public MenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> getAllAvailableItems() {
        return menuItemRepository.findByAvailability("Available");
    }

    public List<MenuItem> getAvailableItemsByCategory(String category) {
        return menuItemRepository.findByAvailabilityAndCategory("Available", category);
    }

    public List<MenuItem> searchAvailableItems(String query) {
        return menuItemRepository.findByAvailabilityAndNameContainingIgnoreCase("Available", query);
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found with ID: " + id));
    }

    @Transactional
    public MenuItem createMenuItem(MenuItemForm form) {
        MenuItem item = new MenuItem(
                form.getName().trim(),
                form.getCategory().trim(),
                form.getDescription().trim(),
                form.getPrice(),
                form.getAvailability()
        );
        return menuItemRepository.save(item);
    }

    @Transactional
    public MenuItem updateMenuItem(Long id, MenuItemForm form) {
        MenuItem item = getMenuItemById(id);
        item.setName(form.getName().trim());
        item.setCategory(form.getCategory().trim());
        item.setDescription(form.getDescription().trim());
        item.setPrice(form.getPrice());
        item.setAvailability(form.getAvailability());
        return menuItemRepository.save(item);
    }

    @Transactional
    public void toggleAvailability(Long id) {
        MenuItem item = getMenuItemById(id);
        if ("Available".equalsIgnoreCase(item.getAvailability())) {
            item.setAvailability("Unavailable");
        } else {
            item.setAvailability("Available");
        }
        menuItemRepository.save(item);
    }

    @Transactional
    public void deleteMenuItem(Long id) {
        MenuItem item = getMenuItemById(id);
        menuItemRepository.delete(item);
    }

    public long countMenuItems() {
        return menuItemRepository.count();
    }
}
