package com.brunch.config;

import com.brunch.entity.Cart;
import com.brunch.entity.MenuItem;
import com.brunch.entity.User;
import com.brunch.repository.CartRepository;
import com.brunch.repository.MenuItemRepository;
import com.brunch.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final MenuItemRepository menuItemRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      CartRepository cartRepository,
                      MenuItemRepository menuItemRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.menuItemRepository = menuItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        seedMenuItems();
    }

    private void seedUsers() {
        if (!userRepository.existsByEmail("staff@brunch.com")) {
            User staff = new User(
                    "Sam Staff",
                    "staff@brunch.com",
                    passwordEncoder.encode("Staff@123"),
                    "9876543210",
                    "STAFF"
            );
            userRepository.save(staff);
        }

        if (!userRepository.existsByEmail("customer@brunch.com")) {
            User customer = new User(
                    "Alex Customer",
                    "customer@brunch.com",
                    passwordEncoder.encode("Customer@123"),
                    "9123456780",
                    "CUSTOMER"
            );
            User savedCustomer = userRepository.save(customer);
            cartRepository.save(new Cart(savedCustomer));
        }
    }

    private void seedMenuItems() {
        if (menuItemRepository.count() == 0) {
            List<MenuItem> items = List.of(
                new MenuItem(
                    "Masala Dosa",
                    "Breakfast",
                    "Crispy golden crepe stuffed with spiced mashed potato filling served with sambar and coconut chutney.",
                    90.0,
                    "Available"
                ),
                new MenuItem(
                    "Veg Fried Rice",
                    "Main Course",
                    "Aromatic long-grain basmati rice wok-tossed with fresh crunchy garden vegetables and Asian herbs.",
                    180.0,
                    "Available"
                ),
                new MenuItem(
                    "Chicken Sandwich",
                    "Snacks",
                    "Toasted artisan bread layered with seasoned juicy chicken shreds, fresh crisp lettuce, and creamy dressing.",
                    120.0,
                    "Available"
                ),
                new MenuItem(
                    "Fresh Lime Juice",
                    "Drinks",
                    "Refreshing chilled beverage freshly pressed from handpicked limes infused with fresh mint leaves.",
                    60.0,
                    "Available"
                ),
                new MenuItem(
                    "Chocolate Brownie",
                    "Desserts",
                    "Warm, decadent dark chocolate fudge brownie baked to perfection with gooey melted chocolate chunks.",
                    90.0,
                    "Available"
                ),
                new MenuItem(
                    "Paneer Butter Masala",
                    "Main Course",
                    "Rich, velvety tomato and cashew cream gravy cooked with tender cubes of cottage cheese.",
                    220.0,
                    "Unavailable"
                )
            );
            menuItemRepository.saveAll(items);
        }
    }
}
