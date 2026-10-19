package com.brunch.config;

import com.brunch.model.Cart;
import com.brunch.model.MenuItem;
import com.brunch.model.RestaurantProfile;
import com.brunch.model.User;
import com.brunch.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(FileStorageService fileStorageService, PasswordEncoder passwordEncoder) {
        this.fileStorageService = fileStorageService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (fileStorageService.getUsers().isEmpty()) {
            log.info("No existing users found. Seeding demo data into JSON files...");
            seedDemoData();
        } else {
            log.info("Existing data detected in JSON storage. Skipping initial seeding.");
        }
    }

    private void seedDemoData() {
        // 1. Primary Restaurant
        User restaurantUser1 = new User();
        restaurantUser1.setFullName("Chef Marco");
        restaurantUser1.setEmail("restaurant@brunch.com");
        restaurantUser1.setPassword(passwordEncoder.encode("Restaurant@123"));
        restaurantUser1.setPhone("9876543210");
        restaurantUser1.setRole("RESTAURANT");
        restaurantUser1.setCreatedAt(LocalDateTime.now());
        User savedRest1 = fileStorageService.saveUser(restaurantUser1);

        RestaurantProfile restProfile1 = new RestaurantProfile();
        restProfile1.setUserId(savedRest1.getId());
        restProfile1.setRestaurantName("The Spice Bistro");
        restProfile1.setDescription("Authentic Indian street eats, artisanal brunch platters, and specialty beverages.");
        restProfile1.setPhone("9876543210");
        restProfile1.setAddress("104 Indiranagar 100ft Road");
        restProfile1.setCity("Bangalore");
        restProfile1.setOpeningTime("08:00 AM");
        restProfile1.setClosingTime("11:00 PM");
        RestaurantProfile savedProfile1 = fileStorageService.saveRestaurant(restProfile1);

        // 2. Second Restaurant
        User restaurantUser2 = new User();
        restaurantUser2.setFullName("Elena Rostova");
        restaurantUser2.setEmail("cafe@brunch.com");
        restaurantUser2.setPassword(passwordEncoder.encode("Cafe@123"));
        restaurantUser2.setPhone("9820012345");
        restaurantUser2.setRole("RESTAURANT");
        restaurantUser2.setCreatedAt(LocalDateTime.now());
        User savedRest2 = fileStorageService.saveUser(restaurantUser2);

        RestaurantProfile restProfile2 = new RestaurantProfile();
        restProfile2.setUserId(savedRest2.getId());
        restProfile2.setRestaurantName("Brew & Bake Cafe");
        restProfile2.setDescription("Artisan European bakery, single-origin coffees, and gourmet burgers.");
        restProfile2.setPhone("9820012345");
        restProfile2.setAddress("45 Hill Road, Bandra West");
        restProfile2.setCity("Mumbai");
        restProfile2.setOpeningTime("07:30 AM");
        restProfile2.setClosingTime("10:30 PM");
        RestaurantProfile savedProfile2 = fileStorageService.saveRestaurant(restProfile2);

        // 3. Demo Customer
        User customerUser = new User();
        customerUser.setFullName("Alex Customer");
        customerUser.setEmail("customer@brunch.com");
        customerUser.setPassword(passwordEncoder.encode("Customer@123"));
        customerUser.setPhone("9123456780");
        customerUser.setRole("CUSTOMER");
        customerUser.setCreatedAt(LocalDateTime.now());
        User savedCustomer = fileStorageService.saveUser(customerUser);

        Cart cart = new Cart();
        cart.setCustomerId(savedCustomer.getId());
        fileStorageService.saveCart(cart);

        // 4. Menu Items for Restaurant 1
        Long r1Id = savedProfile1.getId();
        fileStorageService.saveMenuItem(new MenuItem(null, r1Id, "Masala Dosa", "Breakfast",
                "Crispy golden crepe stuffed with spiced mashed potato filling served with sambar and coconut chutney.", 90.0, "Available"));
        fileStorageService.saveMenuItem(new MenuItem(null, r1Id, "Veg Fried Rice", "Main Course",
                "Aromatic long-grain basmati rice wok-tossed with fresh crunchy garden vegetables and Asian herbs.", 180.0, "Available"));
        fileStorageService.saveMenuItem(new MenuItem(null, r1Id, "Chicken Sandwich", "Snacks",
                "Toasted artisan bread layered with seasoned juicy chicken shreds, fresh crisp lettuce, and creamy dressing.", 120.0, "Available"));
        fileStorageService.saveMenuItem(new MenuItem(null, r1Id, "Fresh Lime Juice", "Drinks",
                "Refreshing chilled beverage freshly pressed from handpicked limes infused with fresh mint leaves.", 60.0, "Available"));
        fileStorageService.saveMenuItem(new MenuItem(null, r1Id, "Chocolate Brownie", "Desserts",
                "Warm, decadent dark chocolate fudge brownie baked to perfection with gooey melted chocolate chunks.", 90.0, "Available"));
        fileStorageService.saveMenuItem(new MenuItem(null, r1Id, "Paneer Butter Masala", "Main Course",
                "Rich, velvety tomato and cashew cream gravy cooked with tender cubes of cottage cheese.", 220.0, "Unavailable"));
        fileStorageService.saveMenuItem(new MenuItem(null, r1Id, "Cold Coffee", "Drinks",
                "Rich and creamy iced blend of fresh brewed espresso, chilled whole milk, and vanilla ice cream.", 110.0, "Available"));
        fileStorageService.saveMenuItem(new MenuItem(null, r1Id, "Crispy Veg Samosas (2 pcs)", "Snacks",
                "Golden deep-fried pastry pyramids filled with spiced potatoes and sweet green peas.", 50.0, "Available"));

        // 5. Menu Items for Restaurant 2
        Long r2Id = savedProfile2.getId();
        fileStorageService.saveMenuItem(new MenuItem(null, r2Id, "Avocado Toast", "Breakfast",
                "Fresh smashed avocado on toasted sourdough with cherry tomatoes, feta, and olive oil.", 190.0, "Available"));
        fileStorageService.saveMenuItem(new MenuItem(null, r2Id, "Classic Cheeseburger", "Main Course",
                "Grilled prime patty with melted cheddar, crisp lettuce, dill pickles, and secret burger sauce.", 250.0, "Available"));
        fileStorageService.saveMenuItem(new MenuItem(null, r2Id, "Iced Caramel Macchiato", "Drinks",
                "Freshly brewed espresso poured over iced milk and finished with artisanal caramel sauce.", 160.0, "Available"));
        fileStorageService.saveMenuItem(new MenuItem(null, r2Id, "New York Cheesecake", "Desserts",
                "Velvety smooth classic baked cheesecake with a buttery graham cracker crust.", 180.0, "Available"));

        log.info("Demo data seeding completed successfully!");
    }
}
