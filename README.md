# Brunch 🍽️

A very simple food delivery web app for learning Java Spring Boot.

- Java 17 + Spring Boot + Thymeleaf (HTML templates)
- No database, no REST APIs, no login, no payments
- All data is stored in Java lists in memory (it disappears when the app restarts)

## What it does

**Customer**
1. See a list of restaurants
2. See the food items of a restaurant
3. Add food items to a cart
4. Enter name, phone number and delivery address
5. Place an order
6. See an order confirmation page

**Admin** (open `/admin`)
1. Add a restaurant
2. Add a food item
3. See all orders
4. Change order status: `PLACED`, `PREPARING`, `OUT_FOR_DELIVERY`, `DELIVERED`

The app starts with two restaurants (Pizza Palace, Spice Junction) and 6 food items.

## Project structure

```
brunch/
├── pom.xml
├── Dockerfile
├── README.md
├── .gitignore
├── .dockerignore
└── src/main/
    ├── java/com/brunch/
    │   ├── BrunchApplication.java    (starts the app)
    │   ├── BrunchController.java     (all pages + the in-memory lists)
    │   ├── Restaurant.java
    │   ├── FoodItem.java
    │   ├── CartItem.java
    │   └── Order.java
    └── resources/
        ├── application.properties
        ├── static/style.css
        └── templates/
            ├── home.html             (restaurant list)
            ├── menu.html             (food items + Add to Cart)
            ├── cart.html             (cart + customer details + Place Order)
            ├── confirmation.html     (order confirmation)
            └── admin.html            (admin page)
```

## Pages

| URL | Page |
|-----|------|
| `/` | Home – restaurant list |
| `/restaurant/1` | Menu of restaurant 1 |
| `/cart` | Cart and checkout form |
| `/order/1` | Confirmation for order 1 |
| `/admin` | Admin page |

## Run it on your computer

You need Java 17 and Maven.

```bash
mvn spring-boot:run
```

Then open http://localhost:8080

## Upload to GitHub

1. Create a new empty repository on github.com (for example `brunch`). Do not add a README there.
2. In the `brunch` folder, run:

```bash
git init
git add .
git commit -m "Brunch food delivery app"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/brunch.git
git push -u origin main
```

## Deploy on Render

1. Go to https://render.com and sign in (you can use your GitHub account).
2. Click **New +** → **Web Service**.
3. Connect your GitHub account and choose the `brunch` repository.
4. Set **Language / Runtime** to **Docker**. Render finds the `Dockerfile` by itself.
5. Choose the **Free** instance type and click **Create Web Service**.
6. Wait for the build to finish (the first build takes a few minutes). Render gives you a URL like `https://brunch-xxxx.onrender.com`.

Render sets a `PORT` environment variable. `application.properties` reads it with `server.port=${PORT:8080}`, so you do not need to configure anything else.

## Good to know

- **Data resets.** Everything lives in memory, so restarting or redeploying the app brings back only the sample data. On Render's free plan the app also goes to sleep when nobody uses it, and it restarts (and resets) on the next visit.
- **Each visitor has their own cart.** The cart is kept in the browser session. Orders are shared and visible on the admin page.
- **The admin page has no login**, as requested. Anyone who knows `/admin` can use it, so this is only for learning and demos.

## Ideas to try next

- Add a "Remove" button in the cart
- Stop the cart from mixing food from two restaurants
- Replace the lists with a database (Spring Data JPA)
- Add a login for the admin page (Spring Security)
