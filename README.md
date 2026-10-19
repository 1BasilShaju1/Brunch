# Brunch — Food Delivery Platform

Brunch is a standalone, server-side rendered food delivery web application built using **Java 17**, **Spring Boot 3+**, **Spring Security**, **Spring Data JPA**, **Thymeleaf**, and an **embedded file-based database**.

> **Zero External Dependencies:** No external MySQL, PostgreSQL, or cloud database setup required. Everything is stored directly in an embedded file-based database (`./data/brunchdb`). Just push to GitHub and deploy!

---

## 🚀 1-Step Render Deployment

Because Brunch uses an embedded file database and includes `render.yaml`, deployment is 100% plug-and-play:

1. **Push your code to GitHub**:
   ```bash
   git add .
   git commit -m "Configure zero-database deployment for Render"
   git push origin main
   ```

2. **Deploy on Render**:
   - Go to [dashboard.render.com](https://dashboard.render.com)
   - Click **New +** &rarr; **Web Service**
   - Connect your GitHub repository: `1BasilShaju1/Brunch`
   - Render will auto-detect settings, or verify:
     - **Runtime:** `Java`
     - **Build Command:** `mvn clean package -DskipTests`
     - **Start Command:** `java -jar target/brunch-0.0.1-SNAPSHOT.jar`
     - **Health Check Path:** `/`
   - **Environment Variables:** **None required!** Leave blank.
   - Click **Create Web Service**.

---

## 💻 Running Locally

1. Open your terminal in the `brunch` directory.
2. Build and run:
   ```bash
   mvn clean package -DskipTests
   java -jar target/brunch-0.0.1-SNAPSHOT.jar
   ```
3. Open `http://localhost:8080` in your web browser.

---

## 🔑 Pre-Seeded Accounts

The application automatically seeds test accounts and menu items on startup:

| Role | Email | Password |
| :--- | :--- | :--- |
| **Customer** | `customer@brunch.com` | `Customer@123` |
| **Staff** | `staff@brunch.com` | `Staff@123` |
