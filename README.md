# Twitter Clone

A **full-stack** application that replicates basic Twitter functionalities, including authentication, tweet creation, and hashtag management.

## Project Structure 

**Database (PostgreSQL):**
![image](https://github.com/user-attachments/assets/1b57bf87-d5fd-4d52-b94d-197c5de5ad95)


# Backend

This project is the backend for a Twitter-like application, built with Spring Boot. It provides APIs for user authentication, tweets, replies, likes, bookmarks, user profiles, and following functionalities.

## Features

*   **User Management:** Registration, login, profile updates (bio, avatar), viewing user profiles.
*   **Tweet Management:** Creating tweets, viewing trending tweets, viewing tweets from followed users, viewing tweets by a specific user.
*   **Reply Management:** Replying to tweets, viewing replies to a tweet, viewing nested replies, viewing replies by a specific user.
*   **Interactions:** Liking/unliking tweets and replies, bookmarking/unbookmarking tweets.
*   **Social Graph:** Following and unfollowing users, viewing followers and followed users.
*   **Hashtags:** Automatic hashtag extraction and creation from tweet content.
*   **Authentication & Authorization:** JWT-based authentication and role-based authorization.
*   **Image Handling:** Avatar uploads with compression.

## Technologies Used

*   **Java 23**
*   **Spring Boot 3.4.2:**
    *   Spring Web (for RESTful APIs)
    *   Spring Data JPA (for database interaction)
    *   Spring Security (for authentication and authorization)
    *   Spring Boot DevTools (for development productivity)
*   **Maven:** Dependency management and build tool.
*   **PostgreSQL:** Relational database.
*   **JJWT (JSON Web Token):** For token-based authentication.
*   **Thumbnailator:** For image thumbnail generation and compression.
*   **Apache Commons IO:** Utility classes for I/O operations.

## Project Structure

The project follows a standard Maven project structure:

```
. (project root)
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── twitterclone
    │   │           └── backend
    │   │               ├── BackendApplication.java  (Main application class)
    │   │               ├── config                   (Application and Web configurations)
    │   │               ├── controllers              (REST API controllers)
    │   │               ├── dto                      (Data Transfer Objects)
    │   │               ├── model
    │   │               │   ├── entities             (JPA Entities)
    │   │               │   ├── exceptions           (Custom exceptions)
    │   │               │   ├── repositories         (JPA Repositories)
    │   │               │   ├── security             (Security configurations, JWT filter)
    │   │               │   └── services             (Business logic services)
    │   │               └── (Other model related classes like DisplayReply, DisplayTweet, UserProfile)
    │   └── resources
    │       ├── application.properties     (Application configuration)
    │       └── (Other resources like static files if any)
    └── test
        └── java
            └── com
                └── twitterclone
                    └── backend
                        └── BackendApplicationTests.java (Unit/Integration tests)
```

## API Endpoints

The base path for all APIs is `/api`.

### Authentication (`/auth`)
*   `POST /register`: Register a new user.
    *   Request Body: `RegisterRequest`
*   `POST /login`: Log in an existing user.
    *   Request Body: `AuthenticationRequest`

### Users (`/user`)
*   `GET /{id}`: Get user details by ID.
*   `GET /{userId}/profile`: Get user profile information.
*   `POST /{userId}/update-profile`: Update user profile (bio, avatar).
    *   Request Parameters: `bio` (String, optional), `avatar` (MultipartFile, optional)
*   `GET /{userId}/followers`: Get a paginated list of a user's followers.
    *   Query Parameters: `page` (default: 0), `size` (default: 10)
*   `GET /{followerId}/followed`: Get a paginated list of users followed by a user.
    *   Query Parameters: `page` (default: 0), `size` (default: 10)
*   `POST /follow/{userIdToFollow}`: Follow a user.
*   `DELETE /unfollow/{userIdToUnfollow}`: Unfollow a user.

### Tweets (`/tweet`)
*   `POST /`: Create a new tweet.
    *   Request Body: `CreateTweetDto`
*   `GET /trending`: Get trending tweets (paginated).
    *   Query Parameters: `page` (default: 0), `size` (default: 20)
*   `GET /followed`: Get tweets from followed users (paginated).
    *   Query Parameters: `page` (default: 0), `size` (default: 20)
*   `GET /{id}`: Get a specific tweet by ID.
*   `GET /{userId}/user`: Get tweets by a specific user (paginated).
    *   Query Parameters: `page` (default: 0), `size` (default: 10)
*   `POST /like`: Like a tweet.
    *   Request Body: `LikeTweetDto`
*   `DELETE /{id}/like`: Unlike a tweet (identified by like ID).
*   `POST /bookmark`: Bookmark a tweet.
    *   Request Body: `BookmarkDto`
*   `DELETE /{id}/bookmark`: Unbookmark a tweet (identified by bookmark ID).

### Replies (`/reply`)
*   `POST /`: Create a reply to a tweet or another reply.
    *   Request Body: `CreateReplyDto`
*   `GET /{id}`: Get a specific reply by ID.
*   `DELETE /{id}`: Delete a reply.
*   `GET /{userId}/user`: Get replies by a specific user (paginated).
    *   Query Parameters: `page` (default: 0), `size` (default: 10)
*   `GET /{tweetId}/tweet`: Get main replies for a specific tweet (paginated).
    *   Query Parameters: `page` (default: 0), `size` (default: 10)
*   `GET /{replyId}/nested`: Get nested replies for a specific parent reply.
*   `POST /like`: Like a reply.
    *   Request Body: `LikeReplyDto`
*   `DELETE /{id}/like`: Unlike a reply (identified by like ID).

### Hashtags (`/hashtag`)
*   `POST /`: Create a hashtag (typically handled internally when creating tweets).
    *   Request Body: `Hashtag`

## Database Entities

*   **User:** Stores user information, credentials, and profile details.
*   **Tweet:** Represents a tweet, including content, author, and creation timestamp.
*   **Reply:** Represents a reply to a tweet or another reply.
*   **LikeTweet:** Join table for likes on tweets.
*   **LikeReply:** Join table for likes on replies.
*   **Bookmark:** Stores user bookmarks for tweets.
*   **Follower:** Represents the follow relationship between users.
*   **Hashtag:** Stores unique hashtags.
*   **Message:** (Entity defined, but no controllers/services implemented)
*   **Notification:** (Entity defined, but no controllers/services implemented)

## Configuration (`application.properties`)

Key configuration properties:

*   `spring.application.name=backend`
*   `spring.datasource.url=jdbc:postgresql://localhost:5432/twitter_clone`
*   `spring.datasource.username=postgresMaster`
*   `spring.datasource.password=goPostgresGo`
*   `spring.jpa.hibernate.ddl-auto=validate` (Validates the schema, does not create or update it. Change to `update` or `create-drop` for development if needed.)
*   `spring.jpa.show-sql=true`
*   `spring.jpa.properties.hibernate.format_sql=true`
*   `spring.main.banner-mode=off`
*   `spring.mvc.view.prefix=/WEB-INF/views/` (JSP view resolver, though REST APIs are the primary focus)
*   `spring.mvc.view.suffix=.jsp`

**Note:** The `WebConfig.java` specifies a resource handler for `/uploads/avatars/**` pointing to a local file system path `file:///C:/dev/twitter-clone/uploads/avatars/`. This path needs to exist or be configured according to your environment.

## Getting Started

### Prerequisites

*   Java 23 JDK
*   Maven
*   PostgreSQL server running and accessible.
*   A database named `twitter_clone` (or as configured in `application.properties`).

### Setup

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd backend
    ```

2.  **Configure Database:**
    *   Ensure PostgreSQL is running.
    *   Create a database (e.g., `twitter_clone`).
    *   Update `src/main/resources/application.properties` with your PostgreSQL connection details (URL, username, password) if they differ from the defaults.

3.  **Configure Uploads Directory:**
    *   Create the directory specified in `WebConfig.java` for avatar uploads (default: `C:/dev/twitter-clone/uploads/avatars/`) or modify the path in `WebConfig.java` to your preferred location.

4.  **Build the project:**
    ```bash
    ./mvnw clean install 
    ```
    (or `mvnw.cmd clean install` on Windows)

5.  **Run the application:**
    ```bash
    ./mvnw spring-boot:run
    ```
    The application will start, typically on port `8080`.

## Dependencies (from `pom.xml`)

*   `spring-boot-starter-data-jpa`: For JPA and Hibernate.
*   `spring-boot-starter-security`: For Spring Security.
*   `spring-boot-starter-web`: For building web applications, including RESTful APIs.
*   `spring-boot-devtools`: For development-time utilities.
*   `postgresql`: PostgreSQL JDBC driver.
*   `spring-boot-starter-test`: For testing.
*   `spring-security-test`: For testing Spring Security features.
*   `net.coobird:thumbnailator`: For image thumbnail generation.
*   `commons-io:commons-io`: For I/O utilities.
*   `io.jsonwebtoken:jjwt-api`, `jjwt-impl`, `jjwt-jackson`: For JSON Web Token (JWT) support.

## Further Reference

*   For detailed Spring Boot documentation, refer to the `HELP.md` file or the official Spring Boot documentation.


# Frontend (Angular)

This project is the frontend for a Twitter-like application, built with Angular. It interacts with a backend API (described above) to provide a user interface for tweeting, replying, liking, following, and managing user profiles.

## Features

*   **User Authentication:** Login and registration forms, JWT token handling.
*   **Home Feed:** Displays tweets from users or followed users, with a switch to toggle between "For You" and "Following" feeds.
*   **Tweet Creation:** Interface to compose and post new tweets.
*   **Tweet Display:** Shows individual tweets with user information, content, and interaction counts (likes, replies, bookmarks).
*   **Tweet Interaction:** Liking/unliking tweets, bookmarking/unbookmarking tweets.
*   **Reply System:**
    *   Replying to tweets and other replies.
    *   Displaying main replies and nested replies.
    *   Liking/unliking replies.
*   **User Profiles:**
    *   Displays user's nickname, profile picture, bio, follower/following counts.
    *   Tabs for viewing user's tweets and replies.
    *   Functionality to follow/unfollow users.
    *   Profile setup/update (bio, avatar).
*   **Navigation:** Main navigation bar for accessing Home, Explore (not implemented), Notifications (not implemented), Profile, and Bookmarks (not implemented).
*   **Image Handling:** Avatar uploads with client-side compression (using `compressorjs`).
*   **Routing:** Utilizes Angular Router for navigating between different views.
*   **State Management (Basic):** Uses Angular services and RxJS `BehaviorSubject` for managing login state and user details.

## Technologies Used

*   **Angular 19.0.0+**
    *   `@angular/common`
    *   `@angular/compiler`
    *   `@angular/core`
    *   `@angular/forms` (for Reactive Forms)
    *   `@angular/platform-browser`
    *   `@angular/platform-browser-dynamic`
    *   `@angular/router`
    *   `@angular/animations`
*   **TypeScript ~5.6.2**
*   **jwt-decode:** For decoding JWT tokens on the client-side.
*   **compressorjs:** For client-side image compression before upload.
*   **CSS:** For styling components.
*   **Angular CLI:** For project generation, building, and development server.

## Project Structure

The project follows a standard Angular CLI project structure:

```
. (project root)
├── angular.json
├── package.json
├── tsconfig.json
├── tsconfig.app.json
├── tsconfig.spec.json
└── src
    ├── index.html
    ├── main.ts
    ├── styles.css
    ├── app
    │   ├── app.component.ts (.html, .css, .spec.ts) (Root component)
    │   ├── app.config.ts
    │   ├── app.routes.ts
    │   ├── authentication
    │   │   ├── auth-home          (Login/Register choice page)
    │   │   ├── login              (Login form component)
    │   │   └── register           (Registration form component)
    │   ├── config
    │   │   └── http-config.ts     (Backend API URL configuration)
    │   ├── home                 (Main home feed component)
    │   ├── model                (Data models and services)
    │   │   ├── authentication     (Auth related models, user service)
    │   │   ├── reply              (Reply related models, reply service)
    │   │   ├── tweet              (Tweet related models, tweet service)
    │   │   └── user               (User profile related models, user profile service)
    │   ├── navbar               (Main navigation bar component)
    │   ├── reply                (Components related to replies)
    │   │   ├── create-reply
    │   │   ├── nested-reply
    │   │   ├── reply-list
    │   │   └── single-reply
    │   ├── right-sidebar        (Right sidebar component - basic placeholder)
    │   ├── security             (Auth interceptor)
    │   ├── tweet                (Components related to tweets)
    │   │   ├── create-tweet
    │   │   ├── single-tweet
    │   │   ├── tweet-detail
    │   │   └── tweet-list
    │   └── user                 (Components related to user profiles)
    │       ├── update-profile
    │       └── user-profile
    └── public                   (Static assets, e.g., icons)
        └── icons
            ├── back-arrow.svg
            ├── logo.svg
            ├── navbar
            │   └── (home.svg, explore.svg, notification.svg, profile.svg, bookmark.svg)
            └── tweet-buttons
                └── (like.svg, reply.svg, bookmark.svg)
```

## Getting Started

### Prerequisites

*   Node.js and npm (Node Package Manager)
*   Angular CLI (`npm install -g @angular/cli`)
*   A running instance of the backend API (see backend setup).

### Setup

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd frontend
    ```

2.  **Install dependencies:**
    ```bash
    npm install
    ```

3.  **Configure Backend API URL:**
    *   Open `src/app/config/http-config.ts`.
    *   Ensure `apiUrl` points to your running backend API (default: `http://localhost:8080/api`).

4.  **Run the development server:**
    ```bash
    ng serve
    ```
    or
    ```bash
    npm start
    ```
    Navigate to `http://localhost:4200/`. The application will automatically reload if you change any ofthe source files.

## Angular CLI Commands

*   **Development server:** `ng serve`
*   **Watch and build:** `ng build --watch --configuration development`
*   **Run unit tests:** `ng test` (using Karma)
*   **Generate components/services/etc.:** `ng generate component <component-name>`

## Key Components and Services

### Components

*   `AppComponent`: The root component, handles main layout based on login status.
*   **Authentication:**
    *   `AuthHomeComponent`: Landing page for unauthenticated users, offering login/register.
    *   `LoginComponent`: Handles the login form and process.
    *   `RegisterComponent`: Handles the registration form and process.
*   `NavbarComponent`: The main navigation sidebar.
*   `HomeComponent`: Displays the main feed (For You/Following) and tweet creation.
*   **Tweet Components:**
    *   `CreateTweetComponent`: Form for creating new tweets.
    *   `TweetListComponent`: Displays a list of tweets (used for feeds and user profiles).
    *   `SingleTweetComponent`: Displays a single tweet's content and interactions.
    *   `TweetDetailComponent`: Displays a tweet and its replies in detail.
*   **Reply Components:**
    *   `CreateReplyComponent`: Form for creating replies.
    *   `ReplyListComponent`: Displays a list of replies.
    *   `SingleReplyComponent`: Displays a single reply's content and interactions.
    *   `NestedReplyComponent`: Handles the display of nested replies.
*   **User Profile Components:**
    *   `UserProfileComponent`: Displays a user's profile page.
    *   `UpdateProfileComponent`: Modal/component for updating user profile (bio, avatar).
*   `RightSidebarComponent`: Placeholder for a right sidebar.

### Services

*   `AuthService` (`src/app/model/authentication/authService.ts`): Handles login and registration API calls.
*   `UserService` (`src/app/model/authentication/userService.ts`): Manages user login state, JWT decoding, and fetching basic user details.
*   `TweetService` (`src/app/model/tweet/tweetService.ts`): Handles API calls related to tweets (CRUD, likes, bookmarks, fetching feeds).
*   `ReplyService` (`src/app/model/reply/replyService.ts`): Handles API calls related to replies (CRUD, likes, fetching replies).
*   `UserProfileService` (`src/app/model/user/userProfileService.ts`): Handles API calls for user profiles (fetching profile, follow/unfollow, updating profile).
*   `AuthInterceptor` (`src/app/security/auth.interceptor.ts`): HTTP interceptor that automatically adds the JWT token to outgoing requests.

## Styling

Global styles are in `src/styles.css`. Component-specific styles are co-located with their respective components (e.g., `home.component.css`). The application uses a dark theme.

## Further Angular CLI Information

For more details on using the Angular CLI, refer to the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli).

# Frontend (React)

This project is the React frontend for a Twitter-like application, built using Vite and TypeScript. It utilizes Redux Toolkit for state management and interacts with a backend API to provide features like tweeting, replying, liking, following, and profile management.

## Features

*   **User Authentication:** Login and registration modals using React Hook Form.
*   **JWT Handling:** Securely stores and manages JWT tokens using localStorage and Redux state.
*   **State Management:** Centralized application state using Redux Toolkit (`authSlice`, `userSlice`).
*   **Home Feed:** Displays tweets with options to switch between "For You" (all tweets) and "Following" feeds.
*   **Infinite Scrolling:** Loads more tweets/replies as the user scrolls down the lists.
*   **Tweet & Reply Creation:** Forms for composing new tweets and replies.
*   **Tweet & Reply Display:** Renders individual tweets and replies, including nested replies.
*   **Interactions:** Functionality to like/unlike tweets and replies, bookmark/unbookmark tweets.
*   **User Profiles:** Displays user profiles with nickname, avatar, bio, follow counts, and tabs for user's posts and replies.
*   **Profile Management:** Allows users to update their profile bio and avatar (with client-side image compression).
*   **Follow System:** Enables users to follow and unfollow others.
*   **Routing:** Uses React Router DOM for navigation between pages (Home, Tweet Detail, User Profile).
*   **API Interaction:** Structured services for communicating with the backend API, including error handling.
*   **Styling:** Uses CSS Modules for component-scoped styling.

## Technologies Used

*   **React 18.3+**
*   **Vite:** Build tool and development server.
*   **TypeScript:** Static typing for JavaScript.
*   **Redux Toolkit:** State management library.
*   **React Router DOM v6:** Client-side routing.
*   **React Hook Form:** Form validation and management.
*   **jwt-decode:** Decoding JWT tokens.
*   **compressorjs:** Client-side image compression.
*   **CSS Modules:** Scoped CSS styling.

## Project Structure

```
. (project root)
├── public/                  # Static assets
├── src/
│   ├── assets/              # Static assets like icons
│   ├── components/          # Reusable UI components (Navbar, Tweet, Reply, Auth forms, etc.)
│   ├── config/              # Configuration files (e.g., API URL)
│   ├── pages/               # Page-level components (Home, AuthHome, TweetDetail, UserProfile)
│   ├── services/            # Functions for interacting with the backend API
│   ├── store/               # Redux Toolkit store setup and slices
│   │   ├── slices/          # Redux state slices (authSlice, userSlice)
│   │   └── store.ts         # Store configuration
│   ├── types/               # TypeScript type definitions (models)
│   ├── utils/               # Utility functions (e.g., customFetch API wrapper)
│   ├── App.module.css       # Main App component styles
│   ├── App.tsx              # Root application component with routing
│   ├── index.css            # Global CSS styles
│   ├── main.tsx             # Application entry point
│   └── vite-env.d.ts        # Vite environment types
├── .eslintrc.cjs            # ESLint configuration
├── index.html               # Main HTML entry point
├── package.json             # Project dependencies and scripts
├── tsconfig.json            # TypeScript configuration
├── tsconfig.node.json       # TypeScript configuration for Node environment (Vite config)
└── vite.config.ts           # Vite configuration
```

## Getting Started

### Prerequisites

*   Node.js (LTS version recommended) and npm
*   A running instance of the backend API.

### Setup

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd react-frontend 
    ```

2.  **Install dependencies:**
    ```bash
    npm install
    ```

3.  **Configure Backend API URL:**
    *   Open `src/config/http-config.ts`.
    *   Ensure `apiUrl` points to your running backend API (e.g., `http://localhost:8080/api`).
    *   Ensure `baseUrl` points to the base URL of your backend for resolving image paths (e.g., `http://localhost:8080`).

4.  **Run the development server:**
    ```bash
    npm run dev
    ```
    The application will typically be available at `http://localhost:5173`.

## Available Scripts

*   `npm run dev`: Starts the development server with hot module replacement.
*   `npm run build`: Builds the application for production in the `dist/` folder.
*   `npm run lint`: Lints the codebase using ESLint.
*   `npm run preview`: Serves the production build locally for previewing.

## Key Concepts

*   **Components:** The UI is built using functional React components.
*   **State Management:** Redux Toolkit is used for global state (authentication status, logged-in user details).
*   **Routing:** React Router handles navigation between different pages.
*   **API Service:** Functions in `src/services` encapsulate calls to the backend API using a custom fetch wrapper (`src/utils/api.ts`).
*   **Authentication Flow:** Login/Register sets a JWT token in localStorage, which is then managed by the `authSlice` in Redux. The `userSlice` fetches user details upon successful login.
*   **Styling:** CSS Modules are used to scope styles to specific components, preventing global conflicts.
*   **Infinite Scroll:** Implemented using the `IntersectionObserver` API in list components (`TweetList`, `ReplyList`) to load more data when the user scrolls near the bottom.
*   **Image Compression:** `compressorjs` is used in the `UpdateProfile` component to reduce avatar image size before uploading.

## Further Reference

*   [React Documentation](https://react.dev/)
*   [Vite Documentation](https://vitejs.dev/)
*   [React Router Documentation](https://reactrouter.com/)
*   [Redux Toolkit Documentation](https://redux-toolkit.js.org/)
*   [React Hook Form Documentation](https://react-hook-form.com/)
