# Brunch — Food Delivery Platform

Brunch is a full-featured, server-side rendered food delivery web application built using **Java 17**, **Spring Boot 3+**, **Spring Security**, **Thymeleaf**, and **JSON file-based local storage**.

> **Zero External Databases:** No MySQL, PostgreSQL, MongoDB, or Firebase required. All application state is stored directly in local JSON files under `./data/`. Simply push to GitHub and deploy to Render with zero configuration!

---

## 🌟 Key Features

### 👤 Customer Features
- **Account Registration & Login:** Instant account creation with password hashing (BCrypt) and session-based authentication.
- **Restaurant Discovery:** Explore local restaurants, filter by city (e.g. Bangalore, Mumbai), or search by restaurant name.
- **Menu Browsing:** Browse food items across categories (*Breakfast, Main Course, Snacks, Drinks, Desserts*) with real-time text search.
- **Shopping Cart:** Add available items, update quantities, remove items, or clear cart.
- **Smart Cart Enforcement:** Prevents accidental ordering from multiple restaurants simultaneously.
- **Checkout & Order Placement:** Choose between **Pickup** (Free) or **Delivery** (₹30 fee), with **Cash on Delivery** or simulated **UPI** payment.
- **Order Tracking & History:** View past orders, inspect detailed itemized digital receipts, and cancel orders while still in `PENDING` status.

### 🏪 Restaurant Features
- **Restaurant Registration & Dashboard:** Dedicated dashboard showing live metrics: total menu items, total orders, pending orders, and total revenue.
- **Profile Management:** Update restaurant name, city, physical address, contact phone number, and operating hours (*Opening & Closing times*).
- **Menu Management:** Add new dishes, edit prices, descriptions, and categories, delete items, and toggle real-time availability (*Available / Unavailable*).
- **Live Order Management:** View incoming orders, see customer details and delivery addresses, and update live order statuses through the full lifecycle:
  `PENDING` &rarr; `ACCEPTED` &rarr; `PREPARING` &rarr; `READY_FOR_PICKUP` / `OUT_FOR_DELIVERY` &rarr; `DELIVERED` / `CANCELLED`.

---

## 📁 File-Based Data Storage

All data is managed in-memory and automatically persisted to formatted JSON files in `./data/`:

| File | Description |
| :--- | :--- |
| `data/users.json` | Customer and restaurant user credentials |
| `data/restaurants.json` | Restaurant business profiles, locations, and timings |
| `data/menu-items.json` | Food dishes, descriptions, categories, prices, and stock status |
| `data/carts.json` | Active customer shopping carts |
| `data/cart-items.json` | Items and quantities inside shopping carts |
| `data/orders.json` | Customer orders, totals, delivery details, and statuses |
| `data/order-items.json` | Line items and subtotals for each placed order |

*Note: On Render's free tier, the container filesystem is ephemeral and resets to default seeded demo data upon instance spin-down or new redeployment.*

---

## 🔑 Pre-Seeded Demo Accounts

The application automatically initializes sample accounts and menus on first boot:

| Role | Email | Password | Details |
| :--- | :--- | :--- | :--- |
| **Customer** | `customer@brunch.com` | `Customer@123` | Demo customer with order history |
| **Restaurant** | `restaurant@brunch.com` | `Restaurant@123` | *The Spice Bistro* (Bangalore) |
| **Restaurant** | `cafe@brunch.com` | `Cafe@123` | *Brew & Bake Cafe* (Mumbai) |

---

## 🚀 1-Click Render Deployment

Because the project includes `Dockerfile` and `render.yaml`, deployment requires zero manual setup:

1. **Push your code to GitHub**:
   ```bash
   git add .
   git commit -m "Convert Brunch to zero-database JSON file storage"
   git push origin main
   ```

2. **Deploy on Render**:
   - Go to [dashboard.render.com](https://dashboard.render.com)
   - Click **New +** &rarr; **Web Service**
   - Connect your GitHub repository: `1BasilShaju1/Brunch`
   - Render automatically detects the `Dockerfile` from `render.yaml`:
     - **Environment:** `Docker`
     - **Health Check Path:** `/`
     - **Environment Variables:** **None required!** (Leaves PORT dynamic)
   - Click **Deploy Web Service**.

---

## 💻 Running Locally

1. Ensure you have **Java 17+** and **Maven** installed.
2. Build and launch:
   ```bash
   mvn clean package -DskipTests
   java -jar target/brunch-0.0.1-SNAPSHOT.jar
   ```
3. Open [http://localhost:8080](http://localhost:8080) in your browser.
