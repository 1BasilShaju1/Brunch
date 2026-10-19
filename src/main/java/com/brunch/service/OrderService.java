package com.brunch.service;

import com.brunch.dto.CheckoutForm;
import com.brunch.entity.*;
import com.brunch.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
    }

    @Transactional
    public Order placeOrder(User customer, CheckoutForm form) {
        Cart cart = cartService.getOrCreateCart(customer);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Your cart is empty. Add food items before checkout.");
        }

        for (CartItem item : cart.getItems()) {
            if (!"Available".equalsIgnoreCase(item.getMenuItem().getAvailability())) {
                throw new IllegalStateException("Item '" + item.getMenuItem().getName() + "' is no longer available.");
            }
        }

        boolean isDelivery = "Delivery".equalsIgnoreCase(form.getOrderType());
        if (isDelivery && (form.getDeliveryAddress() == null || form.getDeliveryAddress().trim().isEmpty())) {
            throw new IllegalArgumentException("Delivery address is required for delivery orders");
        }

        double totalAmount = cartService.calculateCartTotal(cart);
        double deliveryFee = isDelivery ? 30.0 : 0.0;
        double finalAmount = totalAmount + deliveryFee;

        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(totalAmount);
        order.setDeliveryFee(deliveryFee);
        order.setFinalAmount(finalAmount);
        order.setOrderType(form.getOrderType());
        order.setPaymentMethod(form.getPaymentMethod());
        order.setDeliveryAddress(isDelivery ? form.getDeliveryAddress().trim() : "Not Applicable (Pickup)");
        order.setOrderStatus("PENDING");
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem(
                    order,
                    cartItem.getMenuItem(),
                    cartItem.getMenuItem().getName(),
                    cartItem.getQuantity(),
                    cartItem.getMenuItem().getPrice(),
                    cartItem.getSubtotal()
            );
            orderItems.add(orderItem);
        }
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(cart);

        return savedOrder;
    }

    public List<Order> getCustomerOrders(User customer) {
        return orderRepository.findByCustomerOrderByOrderDateDesc(customer);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + id));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    @Transactional
    public void cancelPendingOrder(Long orderId, User customer) {
        Order order = getOrderById(orderId);
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new SecurityException("Unauthorized to cancel this order");
        }
        if (!"PENDING".equalsIgnoreCase(order.getOrderStatus())) {
            throw new IllegalStateException("Only orders with PENDING status can be cancelled");
        }
        order.setOrderStatus("CANCELLED");
        orderRepository.save(order);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String newStatus) {
        Order order = getOrderById(orderId);
        List<String> validStatuses = List.of(
                "PENDING", "ACCEPTED", "PREPARING", "READY_FOR_PICKUP", "OUT_FOR_DELIVERY", "DELIVERED", "CANCELLED"
        );
        if (!validStatuses.contains(newStatus)) {
            throw new IllegalArgumentException("Invalid status value: " + newStatus);
        }
        order.setOrderStatus(newStatus);
        orderRepository.save(order);
    }

    public long countTotalOrders() {
        return orderRepository.count();
    }

    public long countPendingOrders() {
        return orderRepository.countByOrderStatus("PENDING");
    }
}
