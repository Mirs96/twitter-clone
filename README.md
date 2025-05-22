# Twitter Clone

A **full-stack** application that replicates Twitter functionalities.

## Project Structure 

**Database (PostgreSQL):**
![image](https://github.com/user-attachments/assets/1b57bf87-d5fd-4d52-b94d-197c5de5ad95)

# Backend

This project is the backend for a Twitter-like application, built with Spring Boot. It provides a robust set of APIs for user authentication, managing tweets, replies, interactions (likes, bookmarks), user profiles, social connections (following), hashtag management, search, and more.

## Features

*   **User Management:**
    *   Secure user registration and login (JWT-based).
    *   User profile viewing.
    *   Profile updates including bio and avatar (with image compression).
*   **Tweet Management:**
    *   Creating tweets with automatic hashtag extraction.
    *   Viewing paginated trending tweets (globally).
    *   Viewing paginated tweets from users one follows.
    *   Viewing paginated tweets by a specific user.
    *   Viewing paginated tweets associated with a specific hashtag.
*   **Reply Management:**
    *   Posting replies to tweets or other replies (nested replies).
    *   Viewing paginated main replies for a tweet.
    *   Viewing nested replies for a parent reply.
    *   Viewing paginated replies by a specific user.
*   **Interactions:**
    *   Liking and unliking tweets.
    *   Liking and unliking replies.
    *   Bookmarking and unbookmarking tweets.
    *   Viewing paginated bookmarked tweets for a user.
*   **Social Graph:**
    *   Following and unfollowing other users.
    *   Viewing paginated lists of a user's followers and users they are following.
*   **Hashtag System:**
    *   Automatic parsing and linking of hashtags from tweet content.
    *   Viewing top trending hashtags.
    *   Viewing paginated list of popular and recently active hashtags.
*   **Search:**
    *   Autocomplete suggestions for users and hashtags based on query input.
*   **Authentication & Authorization:**
    *   JWT (JSON Web Token) based authentication.
    *   Role-based access (User entity supports roles like USER, ADMIN, MANAGER, though API endpoints primarily use authenticated user context).
*   **Image Handling:**
    *   Avatar uploads for user profiles.
    *   Automatic image compression for large avatars using Thumbnailator.
*   **Auditing:**
    *   Automatic tracking of `createdAt` and `updatedAt` timestamps for entities.
*   **Error Handling:**
    *   Custom exceptions for clear API error responses (e.g., `EntityNotFoundException`, `ReactionAlreadyExistsException`).
*   **CORS:**
    *   Configured for `http://localhost:4200` and `http://localhost:5173` to allow frontend integration.

## Technologies Used

*   **Java 23**
*   **Spring Boot 3.4.2:**
    *   Spring Web (for RESTful APIs)
    *   Spring Data JPA (for database interaction with Hibernate)
    *   Spring Security (for authentication and authorization)
    *   Spring Boot DevTools (for development productivity)
*   **Maven:** Dependency management and build tool.
*   **PostgreSQL:** Relational database.
*   **JJWT (JSON Web Token):** For token-based authentication (API, Impl, Jackson versions 0.11.5).
*   **Lombok:** To reduce boilerplate code.
*   **Thumbnailator 0.4.20:** For image thumbnail generation and compression.
*   **Apache Commons IO 2.19.0:** Utility classes for I/O operations (used in image handling).

## Project Structure

The project follows a standard Maven project structure:

```
. (project root)
├── pom.xml                             (Maven project configuration)
├── .gitattributes                      (Git line ending configuration)
├── HELP.md                             (Spring Boot help file)
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── twitterclone
    │   │           └── backend
    │   │               ├── BackendApplication.java     (Main application class, @EnableJpaAuditing)
    │   │               ├── config                      (Application, Web, and Security configurations)
    │   │               │   ├── ApplicationConfig.java  (Security-related beans)
    │   │               │   └── WebConfig.java          (Static resource handling for uploads)
    │   │               ├── controllers                 (REST API controllers for each resource)
    │   │               ├── dto                         (Data Transfer Objects for API requests/responses)
    │   │               ├── model
    │   │               │   ├── AutocompleteResponse.java (Model for search suggestions)
    │   │               │   ├── DisplayReply.java       (Model for detailed reply display)
    │   │               │   ├── DisplayTweet.java       (Model for detailed tweet display)
    │   │               │   ├── TrendingHashtag.java    (Model for hashtag with its trend count)
    │   │               │   ├── UserProfile.java        (Model for user profile view)
    │   │               │   ├── entities                (JPA Entities defining database schema)
    │   │               │   ├── exceptions              (Custom application exceptions)
    │   │               │   ├── repositories            (Spring Data JPA Repositories for DB access)
    │   │               │   ├── security                (JWT filter, Security configuration)
    │   │               │   └── services                (Business logic services - interfaces and JPA implementations)
    │   └── resources
    │       └── application.properties        (Application configuration)
    └── test
        └── java
            └── com
                └── twitterclone
                    └── backend
                        └── BackendApplicationTests.java (Basic Spring Boot test)
```

## API Endpoints

The base path for all APIs is `/api`. All protected endpoints require a `Bearer` token in the `Authorization` header.

### Authentication (`/auth`)
*   `POST /register`: Register a new user.
    *   Request Body: `RegisterRequest`
    *   Response: `AuthenticationResponse` (contains JWT token)
*   `POST /login`: Log in an existing user.
    *   Request Body: `AuthenticationRequest`
    *   Response: `AuthenticationResponse` (contains JWT token)

### Users (`/user`)
*   `GET /{id}`: Get user details by ID (primarily for the authenticated user).
*   `GET /{userId}/profile`: Get user profile information (viewable by any authenticated user).
*   `POST /{userId}/update-profile`: Update user profile (bio, avatar). Authenticated user can only update their own profile.
    *   Request Parameters: `bio` (String, optional), `avatar` (MultipartFile, optional)
*   `GET /{userId}/followers`: Get a paginated list of a user's followers.
    *   Query Parameters: `page` (default: 0), `size` (default: 10)
*   `GET /{followerId}/followed`: Get a paginated list of users followed by a specific user.
    *   Query Parameters: `page` (default: 0), `size` (default: 10)
*   `POST /follow/{userIdToFollow}`: The authenticated user follows `userIdToFollow`.
*   `DELETE /unfollow/{userIdToUnfollow}`: The authenticated user unfollows `userIdToUnfollow`.

### Tweets (`/tweet`)
*   `POST /`: Create a new tweet.
    *   Request Body: `CreateTweetDto` (user ID must match authenticated user)
*   `GET /trending`: Get globally trending tweets (paginated).
    *   Query Parameters: `page` (default: 0), `size` (default: 20)
*   `GET /followed`: Get tweets from users followed by the authenticated user (paginated).
    *   Query Parameters: `page` (default: 0), `size` (default: 20)
*   `GET /{id}`: Get a specific tweet by ID, including like/bookmark status for the authenticated user.
*   `GET /{userId}/user`: Get tweets by a specific user (paginated).
    *   Query Parameters: `page` (default: 0), `size` (default: 10)
*   `GET /{hashtagId}/hashtag`: Get tweets associated with a specific hashtag (paginated).
    *   Query Parameters: `page` (default: 0), `size` (default: 10)
*   `GET /{userId}/bookmarks`: Get tweets bookmarked by the authenticated user (paginated). User ID must match authenticated user.
    *   Query Parameters: `page` (default: 0), `size` (default: 10)
*   `POST /like`: Like a tweet.
    *   Request Body: `LikeTweetDto` (user ID must match authenticated user)
*   `DELETE /{id}/like`: Unlike a tweet (identified by the like ID, user must own the like).
*   `POST /bookmark`: Bookmark a tweet.
    *   Request Body: `BookmarkDto` (user ID must match authenticated user)
*   `DELETE /{id}/bookmark`: Unbookmark a tweet (identified by the bookmark ID, user must own the bookmark).

### Replies (`/reply`)
*   `POST /`: Create a reply to a tweet or another reply.
    *   Request Body: `CreateReplyDto` (user ID must match authenticated user)
*   `GET /{id}`: Get a specific reply by ID, including like status for the authenticated user.
*   `DELETE /{id}`: Delete a reply (user must own the reply).
*   `GET /{userId}/user`: Get replies by a specific user (paginated).
    *   Query Parameters: `page` (default: 0), `size` (default: 10)
*   `GET /{tweetId}/tweet`: Get main (top-level) replies for a specific tweet (paginated).
    *   Query Parameters: `page` (default: 0), `size` (default: 10)
*   `GET /{replyId}/nested`: Get nested replies for a specific parent reply.
*   `POST /like`: Like a reply.
    *   Request Body: `LikeReplyDto` (user ID must match authenticated user)
*   `DELETE /{id}/like`: Unlike a reply (identified by the like ID, user must own the like).

### Hashtags (`/hashtag`)
*   `GET /trending`: Get top 5 trending hashtags (based on recent activity).
*   `GET /trending/paged`: Get a paginated list of popular and recently active hashtags.
    *   Query Parameters: `page` (default: 0), `size` (default: 10)

### Search (`/search`)
*   `GET /autocomplete`: Get autocomplete suggestions for users and hashtags.
    *   Query Parameter: `query` (String)

## Key Components & Logic

### Authentication & JWT
*   JWT-based authentication is managed by Spring Security.
*   `JwtService` handles token generation, validation, and claim extraction.
*   The JWT secret key is configurable via `application.security.jwt.secret-key` in `application.properties` (defaults to `b7a002a04fc471eca942e43d9f8974d2826991ccd165dfa0f940df5598704161` if not set).
*   JWT expiration is configurable via `application.security.jwt.expiration` (defaults to 86400000 ms / 24 hours).
*   User ID is included as a custom claim in the JWT.

### Image Handling
*   User avatars are uploaded via `POST /api/user/{userId}/update-profile`.
*   The `WebConfig.java` maps the `/uploads/avatars/**` URL path to a physical file system location (default: `file:///C:/dev/twitter-clone/uploads/avatars/`). This path **must** exist on the server or be configured.
*   `Thumbnailator` library compresses images if they are wider/taller than 800px or larger than 500KB.
*   Stored avatar paths in the database are relative to the configured web path (e.g., `/uploads/avatars/some-uuid.jpg`).
*   DTOs often provide a default avatar path (`/images/default-avatar.png`) if a user has no custom avatar. This path is served if you place a default image in `src/main/resources/static/images/default-avatar.png` (requires `SecurityConfiguration` to permit `/images/**`).

### Hashtag & Trending Logic
*   `HashtagServiceJpa` automatically parses `#tag` from tweet content, creating new `Hashtag` entities if they don't exist.
*   Trending hashtags are determined by complex queries considering usage in tweets within various recent time windows (e.g., last 24h, 7 days, 30 days) and overall popularity.
*   Trending tweets are sorted based on a formula combining creation recency and engagement (likes, replies).

### CORS Configuration
*   `@CrossOrigin` annotations on controllers permit requests from `http://localhost:4200` and `http://localhost:5173`.
*   `SecurityConfiguration` allows `HttpMethod.OPTIONS` for all paths to support CORS preflight requests.

## Database Entities

*   **`BaseEntity`**: Abstract class with `id` (auto-generated), `createdAt`, and `updatedAt` (auto-populated via `@EnableJpaAuditing`).
*   **`User`**: Implements `UserDetails`. Stores first name, last name, nickname, date of birth, sex, unique email (username), hashed password, phone, role (`Role` enum), profile picture path, and bio.
*   **`Tweet`**: Content, reference to the `User` who posted it, and a list of associated `Hashtag`s.
*   **`Reply`**: Content, reference to the `User` who replied, the `Tweet` it belongs to, and an optional `parentReply` for nesting.
*   **`LikeTweet`**: Join entity linking a `User` and a liked `Tweet`.
*   **`LikeReply`**: Join entity linking a `User` and a liked `Reply`.
*   **`Bookmark`**: Join entity linking a `User` and a bookmarked `Tweet`.
*   **`Follower`**: Join entity representing a follow relationship (links `follower` User to `user` User).
*   **`Hashtag`**: Stores a unique `tag` string.
*   **`Message`**: (Entity defined for direct messages: sender, receiver, content, parent message) - *Note: Full controller and service implementation for this feature is pending.*
*   **`Notification`**: (Entity defined for user notifications: user, content) - *Note: Full controller and service implementation for this feature is pending.*
*   **`Role`**: Enum (`USER`, `ADMIN`, `MANAGER`).

## Configuration

### `application.properties` (`src/main/resources/application.properties`)
Key properties:
```properties
spring.application.name=backend
spring.datasource.url=jdbc:postgresql://localhost:5432/twitter_clone
spring.datasource.username=postgresMaster
spring.datasource.password=goPostgresGo
spring.jpa.hibernate.ddl-auto=validate # Validates schema. Use 'update' or 'create' for initial setup.
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.main.banner-mode=off

# JWT Configuration (can be set here to override defaults in JwtService)
# application.security.jwt.secret-key=your-super-secret-key-here
# application.security.jwt.expiration=custom-expiration-in-ms
```

### `WebConfig.java` (`src/main/java/com/twitterclone/backend/config/WebConfig.java`)
Configures serving of uploaded avatar images:
```java
// ...
String uploadPath = "file:///C:/dev/twitter-clone/uploads/"; // Base physical path
registry
    .addResourceHandler("/uploads/avatars/**")
    .addResourceLocations(uploadPath + "avatars/");
// ...
```
**Important:** The `uploadPath` (default `C:/dev/twitter-clone/uploads/`) and its `avatars` subdirectory must exist on the machine running the server, or you need to modify this path to a suitable location for your environment.

## Getting Started

### Prerequisites

*   Java 23 JDK or newer.
*   Maven 3.6+
*   PostgreSQL server (version 12+ recommended) running and accessible.
*   A database named `twitter_clone` (or as configured in `application.properties`).

### Setup

1.  **Clone the repository:**
    ```bash
    git clone <your-repository-url>
    cd <project-folder-name> # e.g., backend
    ```

2.  **Configure Database:**
    *   Ensure your PostgreSQL server is running.
    *   Create a database (e.g., `twitter_clone`).
    *   Update `src/main/resources/application.properties` with your PostgreSQL URL, username, and password if they differ from the defaults.
    *   For the first run, if the schema does not exist, you might want to temporarily change `spring.jpa.hibernate.ddl-auto=validate` to `spring.jpa.hibernate.ddl-auto=update` or `create` to allow Hibernate to create the tables. Remember to switch it back to `validate` for subsequent runs in production-like environments.

3.  **Configure Uploads Directory:**
    *   Create the directory specified in `WebConfig.java` for avatar uploads.
        *   Default on Windows: `C:\dev\twitter-clone\uploads\avatars\`
        *   On Linux/macOS, you might use a path like `/var/www/twitter-clone/uploads/avatars/` or `~/twitter-clone-uploads/avatars/`.
    *   Ensure the application has write permissions to this directory.
    *   Alternatively, modify the `uploadPath` in `src/main/java/com/twitterclone/backend/config/WebConfig.java` to your preferred and existing location.

4.  **Build the project:**
    ```bash
    ./mvnw clean install
    ```
    (On Windows, use `mvnw.cmd clean install`)

5.  **Run the application:**
    ```bash
    ./mvnw spring-boot:run
    ```
    The application will start, typically on port `8080`. Check the console output for the exact port and any startup errors.

## Dependencies (from `pom.xml`)

This project uses Maven for dependency management. Key dependencies include:
*   `spring-boot-starter-data-jpa`
*   `spring-boot-starter-security`
*   `spring-boot-starter-web`
*   `spring-boot-devtools`
*   `postgresql` (PostgreSQL JDBC Driver)
*   `org.projectlombok:lombok`
*   `net.coobird:thumbnailator` (for image compression)
*   `commons-io:commons-io` (for I/O utilities)
*   `io.jsonwebtoken:jjwt-api`, `jjwt-impl`, `jjwt-jackson` (for JWT support)
*   Testing: `spring-boot-starter-test`, `spring-security-test`.

Refer to the `pom.xml` file for a complete list of dependencies and their versions.


# Frontend (Angular)

This project is the frontend for "Y", a Twitter-like application, built with Angular. It interacts with a backend API to provide a user interface for tweeting, replying, liking, bookmarking, following users, exploring trends, and managing user profiles.

## Features

*   **User Authentication:**
    *   Login and registration forms with validation.
    *   JWT token handling via `AuthService` and an `authInterceptor` for secure API requests.
*   **Home Feed:**
    *   Displays tweets based on "For You" (trending) and "Following" tabs.
    *   Infinite scrolling for loading more tweets.
*   **Tweet Creation:**
    *   Dedicated section on the Home page to compose and post new tweets.
    *   Dynamic textarea sizing.
*   **Tweet Display & Interaction:**
    *   Shows individual tweets with user information (nickname, profile picture), content, and creation time.
    *   Displays interaction counts (likes, replies, bookmarks).
    *   Liking/unliking tweets with immediate UI feedback.
    *   Bookmarking/unbookmarking tweets with immediate UI feedback.
    *   Navigation to a detailed tweet view.
*   **Reply System:**
    *   Replying to tweets and other replies (supports nested replies).
    *   Displaying main replies and recursively loaded nested replies.
    *   Liking/unliking replies.
    *   Modal for creating replies to specific parent replies, enhancing user interaction.
    *   Dedicated reply creation section on the tweet detail page.
*   **User Profiles:**
    *   Displays user's nickname, profile picture, bio, follower/following counts.
    *   Tabs for viewing a user's "Posts" (tweets) and "Replies".
    *   Functionality to follow/unfollow other users.
    *   Modal for profile setup/update, allowing users to change their bio and avatar.
*   **Explore Page:**
    *   Displays a list of trending hashtags with their respective post counts.
    *   Allows users to click on a hashtag to view all tweets associated with it.
    *   Infinite scrolling for loading more trending hashtags.
*   **Bookmarks Page:**
    *   Displays tweets bookmarked by the logged-in user.
    *   Infinite scrolling for bookmarked tweets.
    *   Tweets are removed from this list in real-time if unbookmarked.
*   **Right Sidebar:**
    *   Persistent search bar with autocomplete functionality for users and hashtags.
    *   Displays a list of current trending hashtags as a quick overview.
*   **Navigation:**
    *   Main navigation sidebar for easy access to Home, Explore, Notifications (currently a placeholder), Profile, and Bookmarks.
    *   User account control with logout functionality directly in the navbar.
*   **Image Handling:**
    *   Avatar uploads during registration and profile updates.
    *   Client-side image compression using `compressorjs` to optimize uploads.
*   **Routing:**
    *   Utilizes Angular Router for navigating between different views and components.
*   **State Management (Reactive):**
    *   Employs Angular services and RxJS (`BehaviorSubject`, `Observable`) for managing application-wide state such as authentication status, current user details, and other dynamic data.
*   **UI/UX:**
    *   Consistent dark theme across the application.
    *   Modals for login, registration, and profile updates to provide a focused experience.
    *   Global and component-specific styles for a cohesive look and feel.
    *   Responsive elements for improved usability on different screen sizes (e.g., auth page).
    *   Infinite scrolling implemented in tweet lists, reply lists, and the explore page for better performance and seamless content consumption.

## Technologies Used

*   **Angular ~19.0.0**
    *   `@angular/animations`
    *   `@angular/common` (including `DatePipe`)
    *   `@angular/compiler`
    *   `@angular/core`
    *   `@angular/forms` (Reactive Forms)
    *   `@angular/platform-browser`
    *   `@angular/platform-browser-dynamic`
    *   `@angular/router`
*   **TypeScript ~5.6.2**
*   **RxJS ~7.8.0**
*   **jwt-decode ~4.0.0:** For decoding JWT tokens on the client-side to extract user information.
*   **compressorjs ~1.2.1:** For client-side image compression before uploading avatars.
*   **date-fns ~4.1.0:** (Available, though `DatePipe` is primarily used for date formatting in components).
*   **zone.js ~0.15.0**
*   **CSS:** For styling components with a custom dark theme.
*   **Angular CLI ~19.0.3:** For project generation, building, serving, and testing.

## Project Structure

The project follows a standard Angular CLI project structure:


. (project root)
├── .editorconfig
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
│ ├── app.component.ts (.html, .css, .spec.ts) (Root component)
│ ├── app.config.ts (Application-level configuration, e.g., HttpClient, Interceptors)
│ ├── app.routes.ts (Main application routes)
│ ├── authentication
│ │ ├── auth-home (Component for login/register choice page)
│ │ ├── login (Login form component)
│ │ └── register (Registration form component)
│ ├── bookmarks (Bookmarks page component)
│ ├── config
│ │ └── http-config.ts (Backend API URL configuration)
│ ├── explore (Explore page component for trends and hashtags)
│ ├── home (Main home feed component)
│ ├── model (Data models and services)
│ │ ├── authentication (Auth related models, AuthService)
│ │ ├── hashtag (Hashtag related models, HashtagService)
│ │ ├── reply (Reply related models, ReplyService)
│ │ ├── search (Search related models, SearchService)
│ │ ├── tweet (Tweet related models, TweetService)
│ │ └── user (User profile related models, UserService, UserProfileService)
│ ├── navbar (Main navigation bar component)
│ ├── reply (Components related to replies)
│ │ ├── create-reply (Component to create a new reply)
│ │ ├── nested-reply (Component to display nested replies)
│ │ ├── reply-list (Component to list replies)
│ │ └── single-reply (Component to display a single reply)
│ ├── right-sidebar (Right sidebar component with search and trends)
│ ├── security
│ │ └── auth.interceptor.ts (HTTP interceptor for adding auth tokens)
│ ├── tweet (Components related to tweets)
│ │ ├── create-tweet (Component to create a new tweet)
│ │ ├── single-tweet (Component to display a single tweet)
│ │ ├── tweet-detail (Component for detailed tweet view with replies)
│ │ └── tweet-list (Component to list tweets)
│ └── user (Components related to user profiles)
│ ├── update-profile (Component/modal to update user profile)
│ └── user-profile (User profile page component)
└── public (Static assets)
└── icons
├── back-arrow.svg
├── logo.svg
├── default-avatar.png
├── navbar
│ ├── home.svg
│ ├── explore.svg
│ ├── notification.svg
│ ├── profile.svg
│ └── bookmark.svg
└── tweet-buttons
├── like.svg
├── reply.svg
└── bookmark.svg

## Getting Started

### Prerequisites

*   Node.js and npm (Node Package Manager) - LTS version recommended.
*   Angular CLI (`npm install -g @angular/cli@latest`)
*   A running instance of the backend API (ensure it's accessible).

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
    *   Ensure `apiUrl` and `baseUrl` point to your running backend API (default: `http://localhost:8080/api` and `http://localhost:8080` respectively).

4.  **Run the development server:**
    ```bash
    ng serve
    ```
    or
    ```bash
    npm start
    ```
    Navigate to `http://localhost:4200/`. The application will automatically reload if you change any of the source files.

## Angular CLI Commands

*   **Development server:** `ng serve` (or `npm start`)
*   **Build for development (with watch):** `ng build --watch --configuration development` (or `npm run watch`)
*   **Build for production:** `ng build` (uses `production` configuration by default)
*   **Run unit tests:** `ng test` (using Karma and Jasmine)
*   **Generate components/services/etc.:** `ng generate component path/to/component-name` (e.g., `ng g c shared/new-component`)

## Key Components and Services

### Components

*   `AppComponent`: The root component, orchestrates the main layout based on user authentication status.
*   **Authentication Module (`src/app/authentication`):**
    *   `AuthHomeComponent`: Landing page for unauthenticated users, offering login/register options via modals.
    *   `LoginComponent`: Handles the login form logic and API interaction.
    *   `RegisterComponent`: Handles the registration form logic, including avatar upload, and API interaction.
*   `NavbarComponent`: The main navigation sidebar, providing links to different sections and user account controls.
*   `HomeComponent`: Displays the main feed ("For You" / "Following" tabs) and includes the `CreateTweetComponent`.
*   `ExploreComponent`: Page for discovering trending hashtags and viewing tweets by hashtag.
*   `BookmarksComponent`: Page for displaying the logged-in user's bookmarked tweets.
*   `RightSidebarComponent`: Displays a search bar with autocomplete and trending hashtags.
*   **Tweet Module (`src/app/tweet`):**
    *   `CreateTweetComponent`: Form for composing and posting new tweets.
    *   `TweetListComponent`: Reusable component for displaying lists of tweets (used in Home, Profiles, Explore, Bookmarks).
    *   `SingleTweetComponent`: Displays an individual tweet's content and interaction buttons.
    *   `TweetDetailComponent`: Provides a detailed view of a single tweet and its associated replies.
*   **Reply Module (`src/app/reply`):**
    *   `CreateReplyComponent`: Form for composing and posting replies to tweets or other replies.
    *   `ReplyListComponent`: Displays a list of replies for a tweet or by a user.
    *   `SingleReplyComponent`: Displays an individual reply's content and interaction buttons.
    *   `NestedReplyComponent`: Handles the recursive display of nested replies.
*   **User Module (`src/app/user`):**
    *   `UserProfileComponent`: Displays a user's profile page, including their tweets and replies in separate tabs.
    *   `UpdateProfileComponent`: Modal component for users to update their profile information (bio, avatar).

### Services

*   `AuthService` (`src/app/model/authentication/authService.ts`): Manages user authentication, including login, registration, JWT token storage, and exposing authentication state (`isLoggedIn$`, `userId$`) as observables.
*   `UserService` (`src/app/model/user/userService.ts`): Responsible for fetching and managing the details of the *currently authenticated user* (`userDetails$`, `userStatus$`).
*   `UserProfileService` (`src/app/model/user/userProfileService.ts`): Handles API calls related to fetching other users' profiles, follow/unfollow actions, and profile update submissions.
*   `TweetService` (`src/app/model/tweet/tweetService.ts`): Central service for all tweet-related API interactions, including creating, fetching (trending, followed users, user-specific, by hashtag, bookmarked), liking/unliking, and bookmarking/unbookmarking tweets.
*   `ReplyService` (`src/app/model/reply/replyService.ts`): Manages API calls for replies, including creating, fetching (main and nested), and liking/unliking replies.
*   `HashtagService` (`src/app/model/hashtag/hashtagService.ts`): Fetches trending hashtags from the API, supporting pagination.
*   `SearchService` (`src/app/model/search/searchService.ts`): Provides autocomplete search functionality for users and hashtags.
*   `AuthInterceptor` (`src/app/security/auth.interceptor.ts`): An HTTP interceptor that automatically attaches the JWT token to the headers of outgoing API requests if the user is authenticated.

## Styling

*   Global styles, including the base dark theme, variables, and utility classes (like modal styles), are defined in `src/styles.css`.
*   Component-specific styles are co-located with their respective components (e.g., `home.component.css`) to maintain encapsulation and modularity.
*   The application generally follows a dark theme inspired by modern social media platforms.

## Further Angular CLI Information

For more details on using the Angular CLI, refer to the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli).


# Frontend (React)

This project is the React frontend for a Twitter-like application (internally named "Y"), built using Vite and TypeScript. It utilizes Redux Toolkit for state management and interacts with a backend API to provide features like tweeting, replying, liking, bookmarking, exploring trends, searching, and profile management.

## Features

*   **User Authentication:** Login and registration modals using React Hook Form.
*   **JWT Handling:** Securely stores and manages JWT tokens using `localStorage` and Redux state (`authSlice`), including token validation and decoding.
*   **State Management:** Centralized application state using Redux Toolkit (`authSlice`, `userSlice`). User details are fetched and managed via `userSlice` upon login.
*   **Home Feed:** Displays tweets with options to switch between "For You" (trending/all tweets) and "Following" (tweets from followed users) feeds.
*   **Explore Page:** Discover trending hashtags or view tweets related to a specific hashtag. Includes infinite scrolling for the list of trends.
*   **Search Functionality:** Autocomplete search for users and hashtags located in the right sidebar.
*   **Right Sidebar:** A dedicated sidebar displaying search functionality and a list of current trending hashtags.
*   **Infinite Scrolling:** Efficiently loads more tweets, replies, and trending hashtags as the user scrolls down the respective lists, using `IntersectionObserver`.
*   **Tweet & Reply System:**
    *   Forms for composing new tweets and replies.
    *   Renders individual tweets and replies, including support for nested replies.
    *   Reply creation available directly under tweets or as a response to specific replies (via modal).
*   **User Interactions:**
    *   Functionality to like/unlike tweets and replies.
    *   Bookmark/unbookmark tweets.
    *   Optimistic UI updates for interactions like likes, bookmarks, and follows for a smoother user experience.
*   **User Profiles:**
    *   Displays user profiles with nickname, avatar, bio, follower/following counts.
    *   Tabs for viewing a user's own posts and their replies.
    *   "Set up profile" option for the logged-in user to update their details.
*   **Profile Management:** Allows users to update their profile bio and avatar. Avatar uploads include client-side image compression using `compressorjs`.
*   **Follow System:** Enables users to follow and unfollow other users.
*   **Routing:** Uses React Router DOM v6 for navigation between pages (Home, Tweet Detail, User Profile, Explore, Bookmarks, etc.).
*   **API Interaction:** Structured services for communicating with the backend API, featuring a robust `customFetch` wrapper for handling requests, JWT authorization, FormData, and custom error objects.
*   **Styling:** Uses CSS Modules for component-scoped styling, alongside global styles for base layout, modals, and utilities. Google Fonts (`Inter`) are used for typography.
*   **Date Formatting:** Utilizes `date-fns` for consistent display of dates and times.

## Technologies Used

*   **React:** ^18.3.1
*   **Vite:** ^5.3.4 (Build tool and development server)
*   **TypeScript:** ^5.5.3 (Static typing)
*   **Redux Toolkit:** ^2.2.6 (State management)
*   **React Redux:** ^9.1.2
*   **React Router DOM:** ^6.25.1 (Client-side routing)
*   **React Hook Form:** ^7.52.1 (Form validation and management)
*   **jwt-decode:** ^4.0.0 (Decoding JWT tokens)
*   **compressorjs:** ^1.2.1 (Client-side image compression)
*   **date-fns:** ^4.1.0 (Date utility library)
*   **CSS Modules:** Scoped CSS styling
*   **ESLint & EditorConfig:** For code linting and consistent editor settings.

## Project Structure

```
. (project root)
├── public/                  # Static assets (e.g., favicon.ico)
├── src/
│   ├── assets/              # Static assets like icons (e.g., navigation icons, logo)
│   ├── components/          # Reusable UI components
│   │   ├── Authentication/  # Login, Register Modals
│   │   ├── Navbar/          # Main navigation bar
│   │   ├── Reply/           # Components for replies (Create, List, Single, Nested)
│   │   ├── RightSidebar/    # Right sidebar with Search and Trends
│   │   ├── Tweet/           # Components for tweets (Create, List, Single)
│   │   └── User/            # User-related components (UpdateProfile)
│   ├── config/              # Configuration files (e.g., API URL in http-config.ts)
│   ├── pages/               # Page-level components
│   │   ├── AuthHome/        # Landing page for unauthenticated users
│   │   ├── Bookmarks/       # User's bookmarked tweets page
│   │   ├── ExplorePage/     # Page for exploring trends and hashtags
│   │   ├── Home/            # Main home feed page
│   │   ├── TweetDetail/     # Page for viewing a single tweet and its replies
│   │   └── UserProfile/     # User profile page
│   ├── services/            # Functions for interacting with the backend API (auth, tweet, user, etc.)
│   ├── store/               # Redux Toolkit store setup and slices
│   │   ├── slices/          # Redux state slices (authSlice, userSlice)
│   │   └── store.ts         # Store configuration
│   ├── types/               # TypeScript type definitions for various data models
│   │   ├── authentication/
│   │   ├── hashtag/
│   │   ├── page.ts          # Generic Page<T> type for pagination
│   │   ├── reply/
│   │   ├── search/
│   │   ├── tweet/
│   │   └── user/
│   ├── utils/               # Utility functions (e.g., customFetch API wrapper)
│   ├── App.module.css       # Main App component styles (defines the 3-column layout)
│   ├── App.tsx              # Root application component with routing and core layout logic
│   ├── index.css            # Global CSS styles, scrollbar hiding, and modal definitions
│   ├── main.tsx             # Application entry point
│   └── vite-env.d.ts        # Vite environment types
├── .editorconfig            # Editor configuration
├── .eslintrc.cjs            # ESLint configuration
├── index.html               # Main HTML entry point (Title: "Y")
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

*   **Component-Based Architecture:** The UI is built using functional React components, organized into presentational and container-like (page) components.
*   **State Management (Redux Toolkit):**
    *   `authSlice`: Manages authentication state (token, user ID, login status), including JWT decoding and persistence in `localStorage`.
    *   `userSlice`: Manages details of the logged-in user (fetched via an async thunk after login or on app load).
*   **Routing (React Router DOM):** Handles navigation between different views/pages of the application. The main `App.tsx` defines the routes and renders appropriate page components.
*   **Layout:** The main application (`App.tsx`) features a responsive three-column layout (Navbar, Main Content, Right Sidebar) for authenticated users, styled with CSS Modules.
*   **API Service Layer:**
    *   Functions in `src/services/` (e.g., `tweetService.ts`, `authService.ts`) encapsulate calls to the backend API.
    *   A `customFetch` utility in `src/utils/api.ts` centralizes API request logic, including setting Authorization headers (JWT), handling `FormData`, and managing custom error responses.
*   **Authentication Flow:**
    *   Users log in or register via modals.
    *   On success, a JWT token is received and stored in `localStorage` and Redux state (`authSlice`).
    *   The `authSlice` decodes the token to extract `userId` and validity.
    *   The `userSlice` then fetches detailed user information using the `userId`.
*   **Styling (CSS Modules & Global CSS):**
    *   CSS Modules (`*.module.css`) are used for component-specific styles to prevent global conflicts.
    *   `index.css` provides global styles, base element styling, utility classes (like modals), and scrollbar overrides.
*   **Infinite Scrolling:** Implemented in list components (`TweetList`, `ReplyList`, `ExplorePage` for hashtags) using the `IntersectionObserver` API to dynamically load more data as the user scrolls, enhancing performance and user experience.
*   **Form Handling (React Hook Form):** Used for managing form state, validation (with custom rules), and submission in components like `Login`, `Register`, and `CreateTweet`.
*   **Optimistic Updates:** For actions like liking, bookmarking, or following, the UI is updated immediately upon user interaction, providing instant feedback. The state is then reconciled with the server response, reverting if an error occurs.
*   **Image Handling:** The `UpdateProfile` component uses `compressorjs` to compress avatar images on the client-side before uploading, reducing bandwidth usage and improving upload speed. Image URLs are constructed using the `baseUrl` from `http-config.ts`.
*   **Date Handling:** `date-fns` is employed for parsing and formatting date/time strings received from the API into a user-friendly display format.
*   **Component Reusability:** Core components like `TweetList` and `ReplyList` are designed to be flexible, accepting props like `fetchType`, `userId`, and `hashtagId` to adapt their data fetching and rendering logic for different contexts (e.g., home feed, user profile, bookmarks, hashtag feeds).
*   **State Synchronization:** `useEffect` hooks are strategically used in components (e.g., `App.tsx`, `SingleTweet.tsx`, list components) to manage side effects, synchronize local component state with props or Redux state, and trigger data fetching/refetching based on changes in dependencies like `isLoggedIn`, `userId`, or `listKey` props.

## Further Reference

*   [React Documentation](https://react.dev/)
*   [Vite Documentation](https://vitejs.dev/)
*   [React Router Documentation](https://reactrouter.com/)
*   [Redux Toolkit Documentation](https://redux-toolkit.js.org/)
*   [React Hook Form Documentation](https://react-hook-form.com/)
*   [date-fns Documentation](https://date-fns.org/)
*   [EditorConfig](https://editorconfig.org/)
