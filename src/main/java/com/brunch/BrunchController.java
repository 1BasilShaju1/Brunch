package com.brunch;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// This one class handles every page of the app.
@Controller
public class BrunchController {

    // The allowed order statuses, in order.
    private static final List<String> STATUSES =
            List.of("PLACED", "PREPARING", "OUT_FOR_DELIVERY", "DELIVERED");

    // "Database" = plain Java lists in memory. They reset when the app restarts.
    private final List<Restaurant> restaurants = new CopyOnWriteArrayList<>();
    private final List<FoodItem> foodItems = new CopyOnWriteArrayList<>();
    private final List<Order> orders = new CopyOnWriteArrayList<>();

    // Counters used to give each new item its own id.
    private int nextRestaurantId = 1;
    private int nextFoodItemId = 1;
    private int nextOrderId = 1;

    // Runs once when the app starts: adds the sample data.
    public BrunchController() {
        createRestaurant("Pizza Palace", "12 Main Street");
        createFoodItem(1, "Margherita Pizza", 249);
        createFoodItem(1, "Veg Supreme Pizza", 329);
        createFoodItem(1, "Garlic Bread", 129);

        createRestaurant("Spice Junction", "45 Market Road");
        createFoodItem(2, "Chicken Biryani", 249);
        createFoodItem(2, "Paneer Butter Masala", 219);
        createFoodItem(2, "Butter Naan", 39);
    }

    // ---------- Customer pages ----------

    // Home page: list of restaurants
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("restaurants", restaurants);
        return "home";
    }

    // Menu page: food items of one restaurant
    @GetMapping("/restaurant/{id}")
    public String menu(@PathVariable int id, Model model) {
        Restaurant restaurant = findRestaurant(id);
        if (restaurant == null) {
            return "redirect:/";
        }
        List<FoodItem> menu = new ArrayList<>();
        for (FoodItem food : foodItems) {
            if (food.getRestaurantId() == id) {
                menu.add(food);
            }
        }
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("menu", menu);
        return "menu";
    }

    // "Add to Cart" button
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam int foodItemId, HttpSession session) {
        FoodItem food = findFoodItem(foodItemId);
        if (food == null) {
            return "redirect:/";
        }
        List<CartItem> cart = getCart(session);

        // If the item is already in the cart, just increase the quantity.
        boolean found = false;
        for (CartItem item : cart) {
            if (item.getFoodItemId() == foodItemId) {
                item.setQuantity(item.getQuantity() + 1);
                found = true;
            }
        }
        if (!found) {
            cart.add(new CartItem(food.getId(), food.getName(), food.getPrice(), 1));
        }
        return "redirect:/restaurant/" + food.getRestaurantId();
    }

    // Cart page: items, total price and the customer details form
    @GetMapping("/cart")
    public String cart(@RequestParam(defaultValue = "false") boolean error,
                       Model model, HttpSession session) {
        List<CartItem> cart = getCart(session);
        model.addAttribute("cart", cart);
        model.addAttribute("total", cartTotal(cart));
        model.addAttribute("error", error);
        return "cart";
    }

    // "Empty Cart" button
    @PostMapping("/cart/clear")
    public String clearCart(HttpSession session) {
        getCart(session).clear();
        return "redirect:/cart";
    }

    // "Place Order" button
    @PostMapping("/order/place")
    public String placeOrder(@RequestParam String customerName,
                             @RequestParam String phone,
                             @RequestParam String address,
                             HttpSession session) {
        List<CartItem> cart = getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        if (customerName.isBlank() || phone.isBlank() || address.isBlank()) {
            return "redirect:/cart?error=true";
        }

        // Copy the cart items into the order, then empty the cart.
        Order order = new Order(nextOrderId++, customerName.trim(), phone.trim(),
                address.trim(), new ArrayList<>(cart), cartTotal(cart), "PLACED");
        orders.add(order);
        cart.clear();
        return "redirect:/order/" + order.getId();
    }

    // Order confirmation page
    @GetMapping("/order/{id}")
    public String confirmation(@PathVariable int id, Model model) {
        Order order = findOrder(id);
        if (order == null) {
            return "redirect:/";
        }
        model.addAttribute("order", order);
        return "confirmation";
    }

    // ---------- Admin pages ----------

    // Admin page: add restaurant, add food item, see orders, change status
    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("restaurants", restaurants);
        model.addAttribute("foodItems", foodItems);
        model.addAttribute("orders", orders);
        model.addAttribute("statuses", STATUSES);
        return "admin";
    }

    @PostMapping("/admin/restaurant")
    public String addRestaurant(@RequestParam String name, @RequestParam String address) {
        if (!name.isBlank() && !address.isBlank()) {
            createRestaurant(name.trim(), address.trim());
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/food")
    public String addFood(@RequestParam int restaurantId,
                          @RequestParam String name,
                          @RequestParam double price) {
        if (findRestaurant(restaurantId) != null && !name.isBlank() && price > 0) {
            createFoodItem(restaurantId, name.trim(), price);
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/order/{id}/status")
    public String updateStatus(@PathVariable int id, @RequestParam String status) {
        Order order = findOrder(id);
        if (order != null && STATUSES.contains(status)) {
            order.setStatus(status);
        }
        return "redirect:/admin";
    }

    // ---------- Small helper methods ----------

    private void createRestaurant(String name, String address) {
        restaurants.add(new Restaurant(nextRestaurantId++, name, address));
    }

    private void createFoodItem(int restaurantId, String name, double price) {
        foodItems.add(new FoodItem(nextFoodItemId++, restaurantId, name, price));
    }

    private Restaurant findRestaurant(int id) {
        for (Restaurant restaurant : restaurants) {
            if (restaurant.getId() == id) {
                return restaurant;
            }
        }
        return null;
    }

    private FoodItem findFoodItem(int id) {
        for (FoodItem food : foodItems) {
            if (food.getId() == id) {
                return food;
            }
        }
        return null;
    }

    private Order findOrder(int id) {
        for (Order order : orders) {
            if (order.getId() == id) {
                return order;
            }
        }
        return null;
    }

    // Each visitor gets their own cart, kept in their browser session.
    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    private double cartTotal(List<CartItem> cart) {
        double total = 0;
        for (CartItem item : cart) {
            total += item.getLineTotal();
        }
        return total;
    }
}
