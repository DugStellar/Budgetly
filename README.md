# Budgetly – Android Budgeting App

## Overview
Budgetly is a mobile budgeting application built using **Kotlin in Android Studio**. It helps users manage personal finances by tracking expenses, setting monthly budget goals, organizing spending categories, and visualising spending patterns using charts.

The app uses a **Room Database (SQLite)** for local storage, meaning all user data is stored directly on the device.

---

## Features

-  User registration and login system  
-  Add, edit, and delete expenses  
-  Categorise expenses (Food, Transport, Shopping, etc.)  
-  Filter expenses by date range  
-  Upload receipts/images for expenses  
-  Set minimum and maximum monthly budget goals  
-  View spending analytics using pie charts  
-  Session management (auto-login using SharedPreferences)  

---

## Project Structure

### MainActivity.kt
- Entry point of the app  
- Checks if a user is already logged in  
- Redirects to Dashboard or Login screen  
- Displays welcome UI and sample data  
- Handles “Get Started” navigation  

---

### LoginActivity.kt
- Handles user login and registration  
- Validates username and password  
- Creates new user accounts if they don’t exist  
- Stores logged-in user using SharedPreferences  

---

### DashboardActivity.kt
- Main home screen after login  
- Displays list of all expenses  
- Shows total spending and balance overview  
- Allows filtering by date range  
- Provides navigation to Analytics screen  
- Includes logout functionality  

---

### AddExpenseActivity.kt
- Allows users to add new expenses  
- Inputs include:
  - Amount  
  - Category  
  - Description/notes  
  - Date  
  - Start and end time  
  - Receipt image upload  
- Saves data into Room Database  

---

### AnalyticsActivity.kt
- Displays spending breakdown per category  
- Uses MPAndroidChart library  
- Converts expense data into a pie chart  
- Helps users visualise spending habits  

---

## Database Layer

### AppDatabase.kt
- Main Room database setup  
- Connects all DAOs (Data Access Objects)  
- Stores users, expenses, and categories  

---

### Expense.kt
Represents an expense record:
- id  
- amount  
- category  
- description  
- date  
- time range  
- image URI  

---

### User.kt
Stores user data:
- username  
- password  
- monthly minimum budget goal  
- monthly maximum budget goal  

---

### Category.kt
Stores expense categories such as:
- Food  
- Transport  
- Entertainment  
- Bills  

---

## UI Components Used

- RecyclerView (expense lists)  
- Floating Action Button (add expense)  
- Spinner (category selection)  
- Material Date Picker  
- Pie Chart (analytics)  
- Image Picker (receipt upload)  

---

## Data Storage

### Room Database
Used for:
- Users  
- Expenses  
- Categories  

### SharedPreferences
Used for:
- Storing logged-in user session  

---

## How the App Works

1. Launch the app  
2. Register or log in  
3. Access the dashboard  
4. Add daily expenses  
5. Set budget goals  
6. View analytics and spending breakdown  

---

## Tech Stack

- Kotlin  
- Android Studio  
- Room Database  
- RecyclerView  
- SharedPreferences  
- MPAndroidChart  
- Material Design Components  

---

## Future Improvements

- Cloud backup & sync  
- Dark mode support  
- Notifications for overspending  
- Export reports (PDF/Excel)  
- Fingerprint login  
- Multi-device sync  

---

## Project Purpose
This project demonstrates:
- Android app development using Kotlin  
- Local database management (Room)  
- CRUD operations  
- User authentication system  
- Data visualisation with charts  
- Clean UI/UX design principles  

