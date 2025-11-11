# 🛍️ E-Commerce Full Stack Web Application

A full-stack **E-Commerce Management System** built using **Spring Boot (Java)**, **MySQL**, and **Thymeleaf**.  
It provides separate **Admin** and **Customer** dashboards for managing products, orders, and customers in an efficient and user-friendly way.

---

## 📖 Overview

This project is a comprehensive full-stack web application that simulates an online shopping platform.  
It includes core e-commerce functionalities such as **product browsing**, **customer management**, **order tracking**, and **admin control** for managing inventory and users.

---

## 🚀 Features

### 👤 **Customer Portal**
- 🛒 Browse available products  
- 🔍 Search and filter items  
- 🧾 Add to cart and place orders  
- 📦 View order history  
- 👨‍💻 Edit personal profile  

### 🧑‍💼 **Admin Portal**
- 📦 Manage products (add, edit, delete, update stock)  
- 🧍 Manage customers and orders  
- 📊 View total sales and activity  
- ⚙️ Access admin-only dashboard  
- 👥 Impersonate customer view for support  

### 🧠 **System Features**
- 🔐 Login & Authentication  
- 💾 Database-backed (MySQL) persistent data  
- 🧩 MVC architecture (Spring Boot + Thymeleaf)  
- 🌐 Responsive UI  
- 🧰 RESTful API endpoints (optional expansion)  

---

## 🧠 Tech Stack

| Layer | Technology |
|-------|-------------|
| **Frontend** | HTML5, CSS3, Thymeleaf |
| **Backend** | Java, Spring Boot |
| **Database** | MySQL |
| **Build Tool** | Maven |
| **IDE** | IntelliJ IDEA / VS Code |
| **Version Control** | Git & GitHub |
| **Hosting (optional)** | Render / AWS / Localhost |

---

## 🗂️ Project Structure

ecommerce-app/
┣ 📂 src/
┃ ┣ 📂 main/
┃ ┃ ┣ 📂 java/com/akash/ecommerce/
┃ ┃ ┃ ┣ 📂 controller/
┃ ┃ ┃ ┣ 📂 model/
┃ ┃ ┃ ┣ 📂 repository/
┃ ┃ ┃ ┗ 📂 service/
┃ ┃ ┣ 📂 resources/
┃ ┃ ┃ ┣ 📂 static/
┃ ┃ ┃ ┣ 📂 templates/
┃ ┃ ┃ ┣ 📜 application.properties
┃ ┗ 📂 test/
┣ 📜 pom.xml
┣ 📜 README.md
┗ 📜 .gitignore

## ⚙️ Setup Instructions

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/akashkalaivanan/ecommerce-fullstack.git
cd ecommerce-fullstack
2️⃣ Configure the Database
Create a MySQL database named:

sql
Copy code
CREATE DATABASE ecommerce_db;
Then edit src/main/resources/application.properties:

properties
Copy code
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.thymeleaf.cache=false
3️⃣ Run the Application
bash
Copy code
mvn spring-boot:run
4️⃣ Access the App
Customer Portal → http://localhost:8080/

Admin Portal → http://localhost:8080/admin

🧑‍💻 Roles & Access
Role	Access	Description
Customer	/home, /cart, /orders	Browse, purchase, and view products
Admin	/admin/**	Manage products, orders, and customers

Default admin credentials (for demo):

Username: admin
Password: admin123

💡 Key Learnings
Developed MVC architecture with Spring Boot

Implemented CRUD operations and form validation

Integrated MySQL database using JPA/Hibernate

Built modular, reusable components with Thymeleaf

Practiced Git workflow and version control

📚 Future Enhancements
🛍️ Payment gateway integration (Razorpay / Stripe)

📦 Product recommendations (AI/ML-based)

📧 Email/SMS notifications

📱 REST API for mobile integration

📬 Contact
👤 Akash K
🎓 B.Tech IT Student – EGS Pillay Engineering College
📧 akashkalaivanan07@gmail.com
🔗 LinkedIn
💻 GitHub

⭐ If you like this project, please give it a star!
It motivates me to build and share more real-world projects 🚀
