package com.brunch.service;

import com.brunch.dto.RestaurantProfileForm;
import com.brunch.model.RestaurantProfile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantService {

    private final FileStorageService fileStorageService;

    public RestaurantService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public List<RestaurantProfile> getAllRestaurants() {
        return fileStorageService.getRestaurants();
    }

    public Optional<RestaurantProfile> getProfileById(Long id) {
        return fileStorageService.findRestaurantById(id);
    }

    public Optional<RestaurantProfile> getProfileByUserId(Long userId) {
        return fileStorageService.findRestaurantByUserId(userId);
    }

    public List<RestaurantProfile> searchRestaurants(String query, String city) {
        return fileStorageService.getRestaurants().stream()
                .filter(r -> {
                    boolean matchesQuery = (query == null || query.isBlank())
                            || (r.getRestaurantName() != null && r.getRestaurantName().toLowerCase().contains(query.toLowerCase().trim()))
                            || (r.getDescription() != null && r.getDescription().toLowerCase().contains(query.toLowerCase().trim()));
                    boolean matchesCity = (city == null || city.isBlank() || "All".equalsIgnoreCase(city))
                            || (r.getCity() != null && r.getCity().equalsIgnoreCase(city.trim()));
                    return matchesQuery && matchesCity;
                })
                .collect(Collectors.toList());
    }

    public RestaurantProfile saveOrUpdateProfile(Long userId, RestaurantProfileForm form) {
        RestaurantProfile profile = fileStorageService.findRestaurantByUserId(userId)
                .orElseGet(() -> {
                    RestaurantProfile p = new RestaurantProfile();
                    p.setUserId(userId);
                    return p;
                });

        profile.setRestaurantName(form.getRestaurantName().trim());
        profile.setDescription(form.getDescription() != null ? form.getDescription().trim() : "");
        profile.setPhone(form.getPhone().trim());
        profile.setAddress(form.getAddress().trim());
        profile.setCity(form.getCity().trim());
        profile.setOpeningTime(form.getOpeningTime() != null ? form.getOpeningTime().trim() : "08:00 AM");
        profile.setClosingTime(form.getClosingTime() != null ? form.getClosingTime().trim() : "11:00 PM");

        return fileStorageService.saveRestaurant(profile);
    }
}
