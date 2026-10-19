package com.brunch.service;

import com.brunch.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
    private static final String DATA_DIR = "data";

    private final ObjectMapper objectMapper;

    private final File usersFile = new File(DATA_DIR, "users.json");
    private final File restaurantsFile = new File(DATA_DIR, "restaurants.json");
    private final File menuItemsFile = new File(DATA_DIR, "menu-items.json");
    private final File cartsFile = new File(DATA_DIR, "carts.json");
    private final File cartItemsFile = new File(DATA_DIR, "cart-items.json");
    private final File ordersFile = new File(DATA_DIR, "orders.json");
    private final File orderItemsFile = new File(DATA_DIR, "order-items.json");

    private final List<User> users = new ArrayList<>();
    private final List<RestaurantProfile> restaurants = new ArrayList<>();
    private final List<MenuItem> menuItems = new ArrayList<>();
    private final List<Cart> carts = new ArrayList<>();
    private final List<CartItem> cartItems = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final List<OrderItem> orderItems = new ArrayList<>();

    private final AtomicLong userIdSeq = new AtomicLong(1);
    private final AtomicLong restaurantIdSeq = new AtomicLong(1);
    private final AtomicLong menuItemIdSeq = new AtomicLong(1);
    private final AtomicLong cartIdSeq = new AtomicLong(1);
    private final AtomicLong cartItemIdSeq = new AtomicLong(1);
    private final AtomicLong orderIdSeq = new AtomicLong(1);
    private final AtomicLong orderItemIdSeq = new AtomicLong(1);

    public FileStorageService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @PostConstruct
    public synchronized void init() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdirs();
            log.info("Created data directory '{}': {}", DATA_DIR, created);
        }

        initFile(usersFile);
        initFile(restaurantsFile);
        initFile(menuItemsFile);
        initFile(cartsFile);
        initFile(cartItemsFile);
        initFile(ordersFile);
        initFile(orderItemsFile);

        loadAllData();
        initSequences();
    }

    private void initFile(File file) {
        if (!file.exists()) {
            try {
                objectMapper.writeValue(file, Collections.emptyList());
                log.info("Initialized empty JSON file: {}", file.getPath());
            } catch (IOException e) {
                log.error("Failed to initialize file: {}", file.getPath(), e);
            }
        }
    }

    private synchronized void loadAllData() {
        try {
            users.clear();
            if (usersFile.exists() && usersFile.length() > 0) {
                List<User> list = objectMapper.readValue(usersFile, new TypeReference<List<User>>() {});
                if (list != null) users.addAll(list);
            }

            restaurants.clear();
            if (restaurantsFile.exists() && restaurantsFile.length() > 0) {
                List<RestaurantProfile> list = objectMapper.readValue(restaurantsFile, new TypeReference<List<RestaurantProfile>>() {});
                if (list != null) restaurants.addAll(list);
            }

            menuItems.clear();
            if (menuItemsFile.exists() && menuItemsFile.length() > 0) {
                List<MenuItem> list = objectMapper.readValue(menuItemsFile, new TypeReference<List<MenuItem>>() {});
                if (list != null) menuItems.addAll(list);
            }

            carts.clear();
            if (cartsFile.exists() && cartsFile.length() > 0) {
                List<Cart> list = objectMapper.readValue(cartsFile, new TypeReference<List<Cart>>() {});
                if (list != null) carts.addAll(list);
            }

            cartItems.clear();
            if (cartItemsFile.exists() && cartItemsFile.length() > 0) {
                List<CartItem> list = objectMapper.readValue(cartItemsFile, new TypeReference<List<CartItem>>() {});
                if (list != null) cartItems.addAll(list);
            }

            orders.clear();
            if (ordersFile.exists() && ordersFile.length() > 0) {
                List<Order> list = objectMapper.readValue(ordersFile, new TypeReference<List<Order>>() {});
                if (list != null) orders.addAll(list);
            }

            orderItems.clear();
            if (orderItemsFile.exists() && orderItemsFile.length() > 0) {
                List<OrderItem> list = objectMapper.readValue(orderItemsFile, new TypeReference<List<OrderItem>>() {});
                if (list != null) orderItems.addAll(list);
            }

            log.info("Loaded from JSON files: {} users, {} restaurants, {} menu items, {} carts, {} cart items, {} orders, {} order items",
                    users.size(), restaurants.size(), menuItems.size(), carts.size(), cartItems.size(), orders.size(), orderItems.size());
        } catch (IOException e) {
            log.error("Error loading JSON data from file storage", e);
        }
    }

    private void initSequences() {
        userIdSeq.set(users.stream().mapToLong(u -> u.getId() != null ? u.getId() : 0).max().orElse(0) + 1);
        restaurantIdSeq.set(restaurants.stream().mapToLong(r -> r.getId() != null ? r.getId() : 0).max().orElse(0) + 1);
        menuItemIdSeq.set(menuItems.stream().mapToLong(m -> m.getId() != null ? m.getId() : 0).max().orElse(0) + 1);
        cartIdSeq.set(carts.stream().mapToLong(c -> c.getId() != null ? c.getId() : 0).max().orElse(0) + 1);
        cartItemIdSeq.set(cartItems.stream().mapToLong(ci -> ci.getId() != null ? ci.getId() : 0).max().orElse(0) + 1);
        orderIdSeq.set(orders.stream().mapToLong(o -> o.getId() != null ? o.getId() : 0).max().orElse(0) + 1);
        orderItemIdSeq.set(orderItems.stream().mapToLong(oi -> oi.getId() != null ? oi.getId() : 0).max().orElse(0) + 1);
    }

    private synchronized <T> void writeToFile(File file, List<T> data) {
        try {
            objectMapper.writeValue(file, data);
        } catch (IOException e) {
            log.error("Failed to write data to file: {}", file.getPath(), e);
            throw new RuntimeException("Error persisting data to file: " + file.getName(), e);
        }
    }

    // ==================== USER OPERATIONS ====================

    public synchronized List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public synchronized Optional<User> findUserById(Long id) {
        if (id == null) return Optional.empty();
        return users.stream().filter(u -> id.equals(u.getId())).findFirst();
    }

    public synchronized Optional<User> findUserByEmail(String email) {
        if (email == null) return Optional.empty();
        String normalized = email.trim().toLowerCase();
        return users.stream()
                .filter(u -> u.getEmail() != null && u.getEmail().trim().equalsIgnoreCase(normalized))
                .findFirst();
    }

    public synchronized User saveUser(User user) {
        if (user.getId() == null) {
            user.setId(userIdSeq.getAndIncrement());
            users.add(user);
        } else {
            users.removeIf(u -> user.getId().equals(u.getId()));
            users.add(user);
        }
        writeToFile(usersFile, users);
        return user;
    }

    public synchronized void deleteUser(Long id) {
        users.removeIf(u -> id.equals(u.getId()));
        writeToFile(usersFile, users);
    }

    // ==================== RESTAURANT PROFILE OPERATIONS ====================

    public synchronized List<RestaurantProfile> getRestaurants() {
        return new ArrayList<>(restaurants);
    }

    public synchronized Optional<RestaurantProfile> findRestaurantById(Long id) {
        if (id == null) return Optional.empty();
        return restaurants.stream().filter(r -> id.equals(r.getId())).findFirst();
    }

    public synchronized Optional<RestaurantProfile> findRestaurantByUserId(Long userId) {
        if (userId == null) return Optional.empty();
        return restaurants.stream().filter(r -> userId.equals(r.getUserId())).findFirst();
    }

    public synchronized RestaurantProfile saveRestaurant(RestaurantProfile profile) {
        if (profile.getId() == null) {
            profile.setId(restaurantIdSeq.getAndIncrement());
            restaurants.add(profile);
        } else {
            restaurants.removeIf(r -> profile.getId().equals(r.getId()));
            restaurants.add(profile);
        }
        writeToFile(restaurantsFile, restaurants);
        return profile;
    }

    public synchronized void deleteRestaurant(Long id) {
        restaurants.removeIf(r -> id.equals(r.getId()));
        writeToFile(restaurantsFile, restaurants);
    }

    // ==================== MENU ITEM OPERATIONS ====================

    public synchronized List<MenuItem> getMenuItems() {
        return new ArrayList<>(menuItems);
    }

    public synchronized Optional<MenuItem> findMenuItemById(Long id) {
        if (id == null) return Optional.empty();
        return menuItems.stream().filter(m -> id.equals(m.getId())).findFirst();
    }

    public synchronized List<MenuItem> findMenuItemsByRestaurantId(Long restaurantId) {
        if (restaurantId == null) return Collections.emptyList();
        return menuItems.stream()
                .filter(m -> restaurantId.equals(m.getRestaurantId()))
                .collect(Collectors.toList());
    }

    public synchronized MenuItem saveMenuItem(MenuItem item) {
        if (item.getId() == null) {
            item.setId(menuItemIdSeq.getAndIncrement());
            menuItems.add(item);
        } else {
            menuItems.removeIf(m -> item.getId().equals(m.getId()));
            menuItems.add(item);
        }
        writeToFile(menuItemsFile, menuItems);
        return item;
    }

    public synchronized void deleteMenuItem(Long id) {
        menuItems.removeIf(m -> id.equals(m.getId()));
        writeToFile(menuItemsFile, menuItems);
    }

    // ==================== CART OPERATIONS ====================

    public synchronized List<Cart> getCarts() {
        return new ArrayList<>(carts);
    }

    public synchronized Optional<Cart> findCartById(Long id) {
        if (id == null) return Optional.empty();
        return carts.stream().filter(c -> id.equals(c.getId())).findFirst();
    }

    public synchronized Optional<Cart> findCartByCustomerId(Long customerId) {
        if (customerId == null) return Optional.empty();
        return carts.stream().filter(c -> customerId.equals(c.getCustomerId())).findFirst();
    }

    public synchronized Cart saveCart(Cart cart) {
        if (cart.getId() == null) {
            cart.setId(cartIdSeq.getAndIncrement());
            carts.add(cart);
        } else {
            carts.removeIf(c -> cart.getId().equals(c.getId()));
            carts.add(cart);
        }
        writeToFile(cartsFile, carts);
        return cart;
    }

    public synchronized void deleteCart(Long id) {
        carts.removeIf(c -> id.equals(c.getId()));
        writeToFile(cartsFile, carts);
    }

    // ==================== CART ITEM OPERATIONS ====================

    public synchronized List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public synchronized Optional<CartItem> findCartItemById(Long id) {
        if (id == null) return Optional.empty();
        return cartItems.stream().filter(ci -> id.equals(ci.getId())).findFirst();
    }

    public synchronized List<CartItem> findCartItemsByCartId(Long cartId) {
        if (cartId == null) return Collections.emptyList();
        return cartItems.stream()
                .filter(ci -> cartId.equals(ci.getCartId()))
                .collect(Collectors.toList());
    }

    public synchronized Optional<CartItem> findCartItemByCartIdAndMenuItemId(Long cartId, Long menuItemId) {
        if (cartId == null || menuItemId == null) return Optional.empty();
        return cartItems.stream()
                .filter(ci -> cartId.equals(ci.getCartId()) && menuItemId.equals(ci.getMenuItemId()))
                .findFirst();
    }

    public synchronized CartItem saveCartItem(CartItem item) {
        if (item.getId() == null) {
            item.setId(cartItemIdSeq.getAndIncrement());
            cartItems.add(item);
        } else {
            cartItems.removeIf(ci -> item.getId().equals(ci.getId()));
            cartItems.add(item);
        }
        writeToFile(cartItemsFile, cartItems);
        return item;
    }

    public synchronized void deleteCartItem(Long id) {
        cartItems.removeIf(ci -> id.equals(ci.getId()));
        writeToFile(cartItemsFile, cartItems);
    }

    public synchronized void deleteCartItemsByCartId(Long cartId) {
        cartItems.removeIf(ci -> cartId.equals(ci.getCartId()));
        writeToFile(cartItemsFile, cartItems);
    }

    // ==================== ORDER OPERATIONS ====================

    public synchronized List<Order> getOrders() {
        return new ArrayList<>(orders);
    }

    public synchronized Optional<Order> findOrderById(Long id) {
        if (id == null) return Optional.empty();
        return orders.stream().filter(o -> id.equals(o.getId())).findFirst();
    }

    public synchronized List<Order> findOrdersByCustomerId(Long customerId) {
        if (customerId == null) return Collections.emptyList();
        return orders.stream()
                .filter(o -> customerId.equals(o.getCustomerId()))
                .sorted((a, b) -> {
                    if (a.getOrderDate() == null || b.getOrderDate() == null) return 0;
                    return b.getOrderDate().compareTo(a.getOrderDate());
                })
                .collect(Collectors.toList());
    }

    public synchronized List<Order> findOrdersByRestaurantId(Long restaurantId) {
        if (restaurantId == null) return Collections.emptyList();
        return orders.stream()
                .filter(o -> restaurantId.equals(o.getRestaurantId()))
                .sorted((a, b) -> {
                    if (a.getOrderDate() == null || b.getOrderDate() == null) return 0;
                    return b.getOrderDate().compareTo(a.getOrderDate());
                })
                .collect(Collectors.toList());
    }

    public synchronized Order saveOrder(Order order) {
        if (order.getId() == null) {
            order.setId(orderIdSeq.getAndIncrement());
            orders.add(order);
        } else {
            orders.removeIf(o -> order.getId().equals(o.getId()));
            orders.add(order);
        }
        writeToFile(ordersFile, orders);
        return order;
    }

    public synchronized void deleteOrder(Long id) {
        orders.removeIf(o -> id.equals(o.getId()));
        writeToFile(ordersFile, orders);
    }

    // ==================== ORDER ITEM OPERATIONS ====================

    public synchronized List<OrderItem> getOrderItems() {
        return new ArrayList<>(orderItems);
    }

    public synchronized List<OrderItem> findOrderItemsByOrderId(Long orderId) {
        if (orderId == null) return Collections.emptyList();
        return orderItems.stream()
                .filter(oi -> orderId.equals(oi.getOrderId()))
                .collect(Collectors.toList());
    }

    public synchronized OrderItem saveOrderItem(OrderItem item) {
        if (item.getId() == null) {
            item.setId(orderItemIdSeq.getAndIncrement());
            orderItems.add(item);
        } else {
            orderItems.removeIf(oi -> item.getId().equals(oi.getId()));
            orderItems.add(item);
        }
        writeToFile(orderItemsFile, orderItems);
        return item;
    }

    public synchronized void saveOrderItems(List<OrderItem> items) {
        for (OrderItem item : items) {
            if (item.getId() == null) {
                item.setId(orderItemIdSeq.getAndIncrement());
            }
            orderItems.removeIf(oi -> item.getId().equals(oi.getId()));
            orderItems.add(item);
        }
        writeToFile(orderItemsFile, orderItems);
    }

    public synchronized void deleteOrderItemsByOrderId(Long orderId) {
        orderItems.removeIf(oi -> orderId.equals(oi.getOrderId()));
        writeToFile(orderItemsFile, orderItems);
    }
}
