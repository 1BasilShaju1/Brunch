package com.brunch.repository;

import com.brunch.entity.Order;
import com.brunch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerOrderByOrderDateDesc(User customer);
    List<Order> findAllByOrderByOrderDateDesc();
    long countByOrderStatus(String orderStatus);
}
