package com.brunch.repository;

import com.brunch.entity.Cart;
import com.brunch.entity.CartItem;
import com.brunch.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndMenuItem(Cart cart, MenuItem menuItem);
}
