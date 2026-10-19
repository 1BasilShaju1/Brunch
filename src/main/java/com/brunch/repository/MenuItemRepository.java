package com.brunch.repository;

import com.brunch.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByAvailability(String availability);
    List<MenuItem> findByAvailabilityAndCategory(String availability, String category);
    List<MenuItem> findByAvailabilityAndNameContainingIgnoreCase(String availability, String name);
    List<MenuItem> findByCategory(String category);
}
