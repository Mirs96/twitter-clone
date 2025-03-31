# Twitter Clone

A **full-stack** application that replicates basic Twitter functionalities, including authentication, tweet creation, and hashtag management.

## 📁 Project Structure 

**Database (PostGreSQL):**
![TwitterCloneDb](https://github.com/user-attachments/assets/54333496-792f-45ed-8456-dff7ef5fc2e1)

**Frontend (Angular):** 
- **Authentication** (Login/Register page for user authentication) 
- **Home** (displays tweet creation form and list of tweets) 
- **Navbar** (functional with "Home" button, under development for additional navigation) 
- **Tweet List** (dynamic scrolling and pagination for tweets) 
- **Single Tweet** (displays tweet details with like, reply, and bookmark actions) 
- **Create Tweet** (form to post new tweets) 
- **Tweet Detail** (view tweet, create reply, and display reply list) 
- **Create Reply** (form to reply to a tweet or another reply, accessible from the tweet/reply buttons and Tweet Detail component) 
- **Reply List** (shows a list of replies to a tweet or reply, with pagination) 
- **Nested Reply** (displays nested replies recursively, supports infinite depth) 
- **Single Reply** (displays reply details with like, reply, and show nested replies if available) 

**Backend (Spring Boot):** 
- **Login/Register** 
- **Tweet Creation** (automatically manages hashtags) 
- **Linking hashtags to tweets** 
- **RESTful API with Spring JPA (Hibernate) & MVC** 
- **Tweet Management (Services)** 
  - Create Tweet 
  - Find Tweet by ID 
  - Get Trending Tweets 
  - Like/Unlike Tweet 
  - Bookmark/Unbookmark Tweet 
- **Reply Management (Services)** 
  - Create Reply 
  - Find Reply by ID 
  - Delete Reply 
  - Get Main Replies 
  - Get Nested Replies 
  - Like/Unlike Reply 
- **Reply Management (Controllers)** 
  - Create Reply (POST) 
  - Find Reply by ID (GET) 
  - Delete Reply (DELETE) 
  - Get Replies by Tweet (GET) 
  - Get Nested Replies (GET) 
  - Like Reply (POST) 
  - Unlike Reply (DELETE) 

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

**Authentication** - **Login:** `POST /api/auth/login`
- **Register:** `POST /api/auth/register`

**Tweet Management**
- **Create a Tweet:** `POST /api/tweet`
 (Note: the backend automatically manages hashtags in tweets) 
- **Find Tweet by ID:** `GET /api/tweet/{id}` 
- **Get Trending Tweets:** `GET /api/tweet/trending` 
- **Like a Tweet:** `POST /api/tweet/like` 
- **Unlike a Tweet:** `DELETE /api/tweet/{id}/like` 
- **Bookmark a Tweet:** `POST /api/tweet/bookmark` 
- **Unbookmark a Tweet:** `DELETE /api/tweet/{id}/bookmark` 

**Reply Management** 
- **Create a Reply:** `POST /api/reply` 
- **Find Reply by ID:** `GET /api/reply/{id}` 
- **Delete Reply:** `DELETE /api/reply/{id}` 
- **Get Replies by Tweet:** `GET /api/reply/{tweetId}/tweet` 
- **Get Nested Replies:** `GET /api/reply/{replyId}/nested` 
- **Like a Reply:** `POST /api/reply/like` 
- **Unlike a Reply:** `DELETE /api/reply/{id}/like` 

### 📜 TODO (Next Steps) 
- ❌ Add user profiles and followers 
- ❌ Add tweets to scroll by follwers 
