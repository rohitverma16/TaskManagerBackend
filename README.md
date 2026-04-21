🚀 Task Manager Backend (Spring Boot)  
📌 Overview

A backend system built using Spring Boot that provides secure user authentication and task management with role-based access control.

🔥 Features  
🔐 JWT Authentication (Signup, Login)  
🔑 BCrypt Password Encryption  
👥 Role-Based Authorization (USER / ADMIN)  
📋 Task Management (User-specific tasks)  
⚠️ Global Exception Handling  
🐳 Dockerized(App + PostgreSQL)

🧱 Tech Stack  
Java 17  
Spring Boot  
Spring Security  
PostgreSQL  
Maven

📂 Project Structure  
Configuration  
Controller Layer  
Service Layer
Repository Layer  
Security(Jwt Authentiation and Authrization)   
Exception Handling  

▶️ How to Run
1. Build project  
   mvn clean package
2. Run application  
   java -jar target/*.jar

🔑 API Endpoints  
Auth
- POST api/auth/signup
- POST api/auth/login

Tasks
-  POST api/tasks
-  GET  api/tasks
-  PUT  api/tasks/{id}

Admin
-  GET    /api/admin/users
-  DELETE /api/admin/users/{userId}


💡 Highlights  
Stateless authentication using JWT  
Secure password storage with BCrypt  
Role-based access control using Spring Security  
User-specific data isolation for tasks

🚧 Upcoming Enhancements  
Redis caching  
API rate limiting  
Logout with token invalidation  
Swagger API documentation

👨‍💻 Author  
Rohit Verma