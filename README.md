# Twitter Clone

A **full-stack** application that replicates basic Twitter functionalities, including authentication, tweet creation, and hashtag management.

## 📁 Project Structure

**Frontend (Angular):**  
- **Authentication (Login/Register)**  
- **Home** (empty, under development)  
- **Sidebar Navbar** (not functional yet)

**Backend (Spring Boot):**  
- **Login/Register**  
- **Tweet Creation** (automatically manages hashtags)  
- **Linking hashtags to tweets**  
- **RESTful API with Spring JPA (Hibernate) & MVC**

## 🛠 Technologies Used

**Frontend (Angular):**  
- **Angular**  
- **TypeScript**  
- **CSS**

**Backend (Spring Boot):**  
- **Spring Boot**  
- **Spring Security**  
- **Spring JPA (Hibernate)**  
- **PostgreSQL**

## **Features**

### 📌 Key API Endpoints

**Authentication**  
- **Login:** `POST /api/auth/login`  
- **Register:** `POST /api/auth/register`

**Tweet Management**  
- **Create a Tweet:** `POST /api/tweet`  
  (Note: the backend automatically manages hashtags in tweets)

### 📜 TODO (Next Steps)

- ✅ **Backend:** Dynamic hashtag management  
- ❌ **Frontend:** Display tweets and enable navbar functionality  
- ❌ Add likes, retweets, and user profiles
