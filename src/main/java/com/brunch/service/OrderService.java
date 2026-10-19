package com.brunch.service;

import com.brunch.dto.CartItemView;
import com.brunch.dto.CartView;
import com.brunch.dto.CheckoutForm;
import com.brunch.dto.OrderView;
import com.brunch.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final FileStorageService fileStorageService;
    private final CartService cartService;
    private final RestaurantService restaurantService;

    public OrderService(FileStorageService fileStorageService, CartService cartService, RestaurantService restaurantService) {
        this.fileStorageService = fileStorageService;
        this.cartService = cartService;
        this.restaurantService = restaurantService;
    }

    public Order placeOrder(User customer, CheckoutForm form) {
        CartView cartView = cartService.getCartView(customer);
        if (cartView.isEmpty()) {
            throw new IllegalStateException("Your cart is empty. Add food items before checkout.");
        }

        for (CartItemView civ : cartView.getItems()) {
            if (!"Available".equalsIgnoreCase(civ.getMenuItem().getAvailability())) {
                throw new IllegalStateException("Item '" + civ.getMenuItem().getName() + "' is no longer available.");
            }
        }

        boolean isDelivery = "Delivery".equalsIgnoreCase(form.getOrderType());
        if (isDelivery && (form.getDeliveryAddress() == null || form.getDeliveryAddress().trim().isEmpty())) {
            throw new IllegalArgumentException("Delivery address is required for delivery orders");
        }

        Order order = new Order();
        order.setCustomerId(customer.getId());
        order.setRestaurantId(cartView.getRestaurantId() != null ? cartView.getRestaurantId() : 1L);
        order.setTotalAmount(cartView.getSubtotal());
        order.setDeliveryFee(isDelivery ? 30.0 : 0.0);
        order.setFinalAmount(order.getTotalAmount() + order.getDeliveryFee());
        order.setOrderType(form.getOrderType());
        order.setPaymentMethod(form.getPaymentMethod());
        order.setDeliveryAddress(isDelivery ? form.getDeliveryAddress().trim() : "Not Applicable (Pickup)");
        order.setOrderStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());

        Order savedOrder = fileStorageService.saveOrder(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItemView civ : cartView.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setOrderId(savedOrder.getId());
            oi.setMenuItemId(civ.getMenuItem().getId());
            oi.setItemName(civ.getMenuItem().getName());
            oi.setQuantity(civ.getQuantity());
            oi.setUnitPrice(civ.getMenuItem().getPrice());
            oi.setSubtotal(civ.getSubtotal());
            orderItems.add(oi);
        }
        fileStorageService.saveOrderItems(orderItems);

        // Clear cart
        cartService.clearCart(customer);

        return savedOrder;
    }

    public Order getOrderById(Long id) {
        return fileStorageService.findOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
    }

    public OrderView getOrderViewById(Long id) {
        Order order = getOrderById(id);
        User customer = fileStorageService.findUserById(order.getCustomerId()).orElse(null);
        RestaurantProfile restaurant = fileStorageService.findRestaurantById(order.getRestaurantId()).orElse(null);
        List<OrderItem> items = fileStorageService.findOrderItemsByOrderId(order.getId());
        return new OrderView(order, customer, restaurant, items);
    }

    public List<OrderView> getCustomerOrders(User customer) {
        List<Order> orders = fileStorageService.findOrdersByCustomerId(customer.getId());
        return orders.stream().map(o -> {
            RestaurantProfile restaurant = fileStorageService.findRestaurantById(o.getRestaurantId()).orElse(null);
            List<OrderItem> items = fileStorageService.findOrderItemsByOrderId(o.getId());
            return new OrderView(o, customer, restaurant, items);
        }).collect(Collectors.toList());
    }

    public List<OrderView> getRestaurantOrders(Long restaurantId) {
        List<Order> orders = fileStorageService.findOrdersByRestaurantId(restaurantId);
        return orders.stream().map(o -> {
            User customer = fileStorageService.findUserById(o.getCustomerId()).orElse(null);
            RestaurantProfile restaurant = fileStorageService.findRestaurantById(restaurantId).orElse(null);
            List<OrderItem> items = fileStorageService.findOrderItemsByOrderId(o.getId());
            return new OrderView(o, customer, restaurant, items);
        }).collect(Collectors.toList());
    }

    public void cancelPendingOrder(Long orderId, User customer) {
        Order order = getOrderById(orderId);
        if (!order.getCustomerId().equals(customer.getId())) {
            throw new SecurityException("Unauthorized to cancel this order");
        }
        if (!"PENDING".equalsIgnoreCase(order.getOrderStatus())) {
            throw new IllegalStateException("Only orders with PENDING status can be cancelled");
        }
        order.setOrderStatus("CANCELLED");
        fileStorageService.saveOrder(order);
    }

    public void updateOrderStatus(Long orderId, String newStatus, Long restaurantId) {
        Order order = getOrderById(orderId);
        if (restaurantId != null && !restaurantId.equals(order.getRestaurantId())) {
            throw new SecurityException("Unauthorized to update order for another restaurant");
        }
        List<String> validStatuses = List.of(
                "PENDING", "ACCEPTED", "PREPARING", "READY_FOR_PICKUP", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED"
        );
        if (!validStatuses.contains(newStatus)) {
            throw new IllegalArgumentException("Invalid status value: " + newStatus);
        }
        order.setOrderStatus(newStatus);
        fileStorageService.saveOrder(order);
    }

    public long countTotalOrdersByRestaurant(Long restaurantId) {
        return fileStorageService.findOrdersByRestaurantId(restaurantId).size();
    }

    public long countPendingOrdersByRestaurant(Long restaurantId) {
        return fileStorageService.findOrdersByRestaurantId(restaurantId).stream()
                .filter(o -> "PENDING".equalsIgnoreCase(o.getOrderStatus()))
                .count();
    }

    public double calculateRestaurantRevenue(Long restaurantId) {
        return fileStorageService.findOrdersByRestaurantId(restaurantId).stream()
                .filter(o -> !"CANCELLED".equalsIgnoreCase(o.getOrderStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }
}
