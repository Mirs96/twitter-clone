package com.twitterclone.backend;

import com.twitterclone.backend.controllers.RegisterRequest;
import com.twitterclone.backend.model.DisplayReply;
import com.twitterclone.backend.model.entities.*; // Import all entities
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.exceptions.ReactionAlreadyExistsException;
import com.twitterclone.backend.model.repositories.*;
import com.twitterclone.backend.model.services.AuthService;
import com.twitterclone.backend.model.services.TweetService;
import com.twitterclone.backend.model.services.ReplyService;
import com.twitterclone.backend.model.services.UserService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=validate"})
public class DataSeedingTest {

    private static final Logger logger = LoggerFactory.getLogger(DataSeedingTest.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private TweetService tweetService;

    @Autowired
    private ReplyService replyService;

    @Autowired
    private UserService userService;

    // Repositories
    @Autowired
    private UserRepositoryJpa userRepositoryJpa;
    @Autowired
    private TweetRepositoryJpa tweetRepositoryJpa;
    @Autowired
    private ReplyRepositoryJpa replyRepositoryJpa;
    @Autowired
    private FollowerRepositoryJpa followerRepositoryJpa;
    @Autowired
    private LikeTweetRepositoryJpa likeTweetRepositoryJpa;
    @Autowired
    private LikeReplyRepositoryJpa likeReplyRepositoryJpa;
    @Autowired
    private BookmarkRepositoryJpa bookmarkRepositoryJpa;
    @Autowired
    private HashtagRepositoryJpa hashtagRepositoryJpa;


    // Store created entities to reference them later
    private final List<User> createdUsers = new ArrayList<>();
    private final List<Tweet> createdTweets = new ArrayList<>();
    private final List<Reply> createdReplies = new ArrayList<>();

    @TestConfiguration
    static class MockUserDetailsServiceConfiguration {
        @Bean
        @Primary
        public UserDetailsService userDetailsService(UserRepositoryJpa userRepositoryJpa) {
            UserDetailsService mock = Mockito.mock(UserDetailsService.class);
            Mockito.when(mock.loadUserByUsername(Mockito.anyString()))
                    .thenAnswer(invocation -> {
                        String username = invocation.getArgument(0);
                        Optional<User> user = userRepositoryJpa.findByEmail(username);
                        if (user.isPresent()) {
                            return user.get();
                        }
                        logger.warn("Mocked UserDetailsService from @TestConfiguration: User '{}' not found. This is expected if called before user is seeded.", username);
                        throw new UsernameNotFoundException("Mocked UserDetailsService from @TestConfiguration: User '" + username + "' not found.");
                    });
            return mock;
        }
    }


    @Test
    @Order(1)
    @DisplayName("Seed Database with Initial Test Data")
    @Transactional
    @Commit
    public void seedDatabase() throws Exception {
        logger.info("--- TEST 1: Starting database seeding process ---");
        // clearDatabase();

        seedUsers();
        seedTweets();
        seedReplies();
        seedFollowers();
        seedLikes();
        seedBookmarks();

        logger.info("--- Database seeding process completed ---");
    }

    private void clearDatabase() {
        logger.info("Attempting to clear database tables...");
        likeReplyRepositoryJpa.deleteAllInBatch();
        likeTweetRepositoryJpa.deleteAllInBatch();
        bookmarkRepositoryJpa.deleteAllInBatch();
        replyRepositoryJpa.deleteAllInBatch();

        List<Tweet> allTweets = tweetRepositoryJpa.findAll();
        for (Tweet tweet : allTweets) {
            if (tweet.getHashtags() != null) {
                tweet.getHashtags().clear();
                tweetRepositoryJpa.save(tweet);
            }
        }
        tweetRepositoryJpa.deleteAllInBatch();

        followerRepositoryJpa.deleteAllInBatch();
        userRepositoryJpa.deleteAllInBatch();
        hashtagRepositoryJpa.deleteAllInBatch();

        logger.warn("Database clearing attempted. Manual verification may be needed.");
        userRepositoryJpa.flush();
    }


    private void seedUsers() {
        logger.info("Seeding users...");
        String defaultAvatarPath = "/images/default-avatar.png";
        String aliceAvatarPath = "/uploads/avatars/alice.png";
        String dianaAvatarPath = "/uploads/avatars/diana.jpg";
        String ethanAvatarPath = "/uploads/avatars/ethan.jpg";
        String fionaAvatarPath = "/uploads/avatars/fiona.jpg";
        String georgeAvatarPath = "/uploads/avatars/george.png";
        String hannahAvatarPath = "/uploads/avatars/hannah.jpg";

        List<RegisterRequest> userRequests = List.of(
                RegisterRequest.builder()
                        .firstname("Alice").lastname("Wonder").nickname("AliceDev")
                        .dob(LocalDate.parse("1995-03-15")).sex("F")
                        .email("alice@example.com").password("password")
                        .phone("1234567890").role(Role.USER)
                        .profilePicture(aliceAvatarPath)
                        .bio("Software developer, coffee enthusiast. Exploring Java, Spring, and React.")
                        .build(),
                RegisterRequest.builder()
                        .firstname("Bob").lastname("Builder").nickname("FoodieBob")
                        .dob(LocalDate.parse("1990-07-22")).sex("M")
                        .email("bob@example.com").password("password")
                        .phone("0987654321").role(Role.USER)
                        .profilePicture(defaultAvatarPath) // Using bobAvatarPath would require the file
                        .bio("Culinary adventurer and travel blogger. Always on the hunt for the next best meal!")
                        .build(),
                RegisterRequest.builder()
                        .firstname("Charlie").lastname("Artiste").nickname("CreativeCharlie")
                        .dob(LocalDate.parse("1998-11-01")).sex("M")
                        .email("charlie@example.com").password("password")
                        .phone("1122334455").role(Role.USER)
                        .profilePicture(defaultAvatarPath)
                        .bio("Digital artist and nature photographer. Capturing beauty in pixels and landscapes.")
                        .build(),
                RegisterRequest.builder()
                        .firstname("Diana").lastname("Reads").nickname("BookwormDiana")
                        .dob(LocalDate.parse("1992-05-30")).sex("F")
                        .email("diana@example.com").password("password")
                        .phone("5544332211").role(Role.USER)
                        .profilePicture(dianaAvatarPath)
                        .bio("Avid reader and film critic. Lost in stories most of the time.")
                        .build(),
                RegisterRequest.builder()
                        .firstname("Ethan").lastname("Explorer").nickname("EthanTravels")
                        .dob(LocalDate.parse("1993-09-10")).sex("M")
                        .email("ethan@example.com").password("password")
                        .phone("6677889900").role(Role.USER)
                        .profilePicture(ethanAvatarPath)
                        .bio("Globetrotter and mountain climber. Seeking new adventures!")
                        .build(),
                RegisterRequest.builder()
                        .firstname("Fiona").lastname("Fitness").nickname("FitFiona")
                        .dob(LocalDate.parse("1996-01-25")).sex("F")
                        .email("fiona@example.com").password("password")
                        .phone("3322110099").role(Role.USER)
                        .profilePicture(fionaAvatarPath)
                        .bio("Fitness coach and yoga instructor. Passionate about healthy living.")
                        .build(),
                RegisterRequest.builder()
                        .firstname("George").lastname("Gamer").nickname("GamerGeorge")
                        .dob(LocalDate.parse("2000-06-05")).sex("M")
                        .email("george@example.com").password("password")
                        .phone("9988776655").role(Role.USER)
                        .profilePicture(georgeAvatarPath)
                        .bio("Esports enthusiast and streamer. Catch me live!")
                        .build(),
                RegisterRequest.builder()
                        .firstname("Hannah").lastname("Hobbyist").nickname("CraftyHannah")
                        .dob(LocalDate.parse("1988-12-12")).sex("F")
                        .email("hannah@example.com").password("password")
                        .phone("1212121212").role(Role.USER)
                        .profilePicture(hannahAvatarPath)
                        .bio("Lover of all things DIY and crafting. Currently obsessed with knitting.")
                        .build()
        );

        for (RegisterRequest request : userRequests) {
            Optional<User> existingUser = userRepositoryJpa.findByEmail(request.getEmail());
            if (existingUser.isEmpty()) {
                logger.info("Registering user: {}", request.getEmail());
                authService.register(request);
                userRepositoryJpa.findByEmail(request.getEmail()).ifPresent(createdUsers::add);
            } else {
                logger.info("User already exists: {}", request.getEmail());
                createdUsers.add(existingUser.get());
            }
        }
        logger.info("{} users processed/found for seeding.", createdUsers.size());
    }

    private User findUserByNickname(String nickname) {
        return createdUsers.stream()
                .filter(u -> u.getNickname().equals(nickname))
                .findFirst()
                .orElseGet(() -> userRepositoryJpa.findAll().stream().filter(dbUser -> dbUser.getNickname().equals(nickname)).findFirst().orElse(null));
    }

    private void seedTweets() throws EntityNotFoundException {
        logger.info("Seeding tweets...");
        if (createdUsers.isEmpty()) {
            logger.info("No users found. Attempting to load users from DB for tweet seeding.");
            createdUsers.addAll(userRepositoryJpa.findAll());
            if(createdUsers.isEmpty()){
                logger.warn("Still no users found. Skipping tweet seeding.");
                return;
            }
        }

        User alice = findUserByNickname("AliceDev");
        User bob = findUserByNickname("FoodieBob");
        User charlie = findUserByNickname("CreativeCharlie");
        User diana = findUserByNickname("BookwormDiana");
        User ethan = findUserByNickname("EthanTravels");
        User fiona = findUserByNickname("FitFiona");
        User george = findUserByNickname("GamerGeorge");
        User hannah = findUserByNickname("CraftyHannah");

        if (alice == null || bob == null || charlie == null || diana == null || ethan == null || fiona == null || george == null || hannah == null) {
            logger.warn("One or more required users not found. Tweet seeding will be incomplete.");
            return;
        }

        List<Tweet> tweetsToCreate = List.of(
                Tweet.builder().user(alice).content("Just deployed my new Spring Boot app! So excited to see it live. What are your favorite #Java features? #SpringBoot #DevLife #Coding").build(),
                Tweet.builder().user(bob).content("Exploring the street food scene in Bangkok! 🍜🌶️ The flavors are incredible. #ThaiFood #StreetFood #Travel #Foodie").build(),
                Tweet.builder().user(alice).content("Working on a new React component with TypeScript. The type safety is a game changer! #ReactJS #TypeScript #Frontend #Coding").build(),
                Tweet.builder().user(charlie).content("Spent the morning sketching by the lake. The tranquility helps my creativity flow. 🎨 #Art #Nature #Inspiration #Sketching").build(),
                Tweet.builder().user(diana).content("Just finished reading 'Project Hail Mary' by Andy Weir. Absolutely loved it! What's everyone reading this week? #SciFi #Books #ReadingList #BookRecommendation").build(),
                Tweet.builder().user(bob).content("Found the most amazing hidden gem for pasta in Rome. My taste buds are still dancing! 🍝 #ItalianFood #Rome #FoodieAdventure #Travel #Foodie").build(),
                Tweet.builder().user(ethan).content("Reached the summit of Mount Fuji today! The view was breathtaking. #Hiking #Japan #AdventureTime #Travel #Nature").build(),
                Tweet.builder().user(fiona).content("Morning yoga session done! Feeling energized and ready for the day. 🙏 #Yoga #Fitness #Mindfulness #Wellness").build(),
                Tweet.builder().user(george).content("Just won an intense match in Valorant! GG to my team. #Gaming #Valorant #Victory #eSports").build(),
                Tweet.builder().user(hannah).content("Finished knitting this cozy scarf. Perfect for the upcoming autumn weather. 🍂 #Knitting #DIYCrafts #Handmade #Crafts").build(),
                Tweet.builder().user(alice).content("Refactoring some old code... it's like archeology sometimes! #Coding #SoftwareDevelopment #Tech").build(),
                Tweet.builder().user(bob).content("Planning my next food trip. Thinking about Vietnam or South Korea. Any suggestions? #TravelPlans #FoodLover #Foodie #Travel").build(),
                Tweet.builder().user(charlie).content("New digital painting released! Check it out on my profile. #DigitalArt #Art #Creative").build(),
                Tweet.builder().user(diana).content("Movie night! Watching a classic tonight. Any guesses? #MovieNight #ClassicFilm #Cinema").build(),
                Tweet.builder().user(ethan).content("Always dreaming of the next mountain to conquer. #Adventure #Travel #Mountains").build(),
                Tweet.builder().user(fiona).content("Healthy meal prep for the week! Eating clean makes me feel great. #HealthyEating #MealPrep #Fitness #Wellness").build(),
                Tweet.builder().user(george).content("Streaming some Apex Legends tonight. Come hang out! #ApexLegends #Gaming #Streaming").build(),
                Tweet.builder().user(hannah).content("Just started a new cross-stitch project. So intricate! #CrossStitch #Crafts #Hobby").build(),
                Tweet.builder().user(alice).content("Learning about microservices architecture. Fascinating stuff! #Microservices #Tech #SoftwareArchitecture #Coding").build(),
                Tweet.builder().user(bob).content("Tried making homemade sushi for the first time. It was an adventure! 🍣 #Sushi #HomeCooking #Foodie #JapaneseFood").build()
        );

        for (Tweet tweet : tweetsToCreate) {
            boolean tweetExists = tweetRepositoryJpa.findAll().stream()
                    .anyMatch(existingTweet -> existingTweet.getUser().getId().equals(tweet.getUser().getId()) &&
                            existingTweet.getContent().equals(tweet.getContent()));
            if (!tweetExists) {
                logger.info("Creating tweet: \"{}...\" by {}", tweet.getContent().substring(0, Math.min(30, tweet.getContent().length())), tweet.getUser().getNickname());
                Tweet createdTweet = tweetService.createTweet(tweet, tweet.getUser().getId());
                createdTweets.add(createdTweet);
            } else {
                logger.info("Tweet already exists: \"{}...\" by {}", tweet.getContent().substring(0, Math.min(30, tweet.getContent().length())), tweet.getUser().getNickname());
                tweetRepositoryJpa.findAll().stream()
                        .filter(existingTweet -> existingTweet.getUser().getId().equals(tweet.getUser().getId()) &&
                                existingTweet.getContent().equals(tweet.getContent()))
                        .findFirst().ifPresent(createdTweets::add);
            }
        }
        logger.info("{} tweets processed/found for seeding.", createdTweets.size());
    }

    private Tweet findTweetByContentStart(String contentStart) {
        return createdTweets.stream()
                .filter(t -> t.getContent().startsWith(contentStart))
                .findFirst()
                .orElseGet(()-> tweetRepositoryJpa.findAll().stream().filter(dbTweet -> dbTweet.getContent().startsWith(contentStart)).findFirst().orElse(null));
    }

    private void seedReplies() throws EntityNotFoundException {
        logger.info("Seeding replies...");
        if (createdUsers.isEmpty() || createdTweets.isEmpty()) {
            logger.info("No users or tweets available. Attempting to load from DB for reply seeding.");
            if(createdUsers.isEmpty()) createdUsers.addAll(userRepositoryJpa.findAll());
            if(createdTweets.isEmpty()) createdTweets.addAll(tweetRepositoryJpa.findAll());
            if(createdUsers.isEmpty() || createdTweets.isEmpty()){
                logger.warn("Still no users or tweets. Skipping reply seeding.");
                return;
            }
        }

        User alice = findUserByNickname("AliceDev");
        User bob = findUserByNickname("FoodieBob");
        User charlie = findUserByNickname("CreativeCharlie");
        User diana = findUserByNickname("BookwormDiana");
        User ethan = findUserByNickname("EthanTravels");
        User fiona = findUserByNickname("FitFiona");
        User george = findUserByNickname("GamerGeorge");

        Tweet tweet1_alice_spring = findTweetByContentStart("Just deployed my new Spring Boot app!");
        Tweet tweet2_bob_bangkok = findTweetByContentStart("Exploring the street food scene in Bangkok!");
        Tweet tweet3_alice_react = findTweetByContentStart("Working on a new React component with TypeScript.");
        Tweet tweet5_diana_book = findTweetByContentStart("Just finished reading 'Project Hail Mary'");
        Tweet tweet7_ethan_fuji = findTweetByContentStart("Reached the summit of Mount Fuji today!");
        Tweet tweet8_fiona_yoga = findTweetByContentStart("Morning yoga session done!");
        Tweet tweet_george_valorant = findTweetByContentStart("Just won an intense match in Valorant!");


        if (alice == null || bob == null || charlie == null || diana == null || ethan == null || fiona == null || george == null ||
                tweet1_alice_spring == null || tweet2_bob_bangkok == null || tweet3_alice_react == null ||
                tweet5_diana_book == null || tweet7_ethan_fuji == null || tweet8_fiona_yoga == null || tweet_george_valorant == null) {
            logger.warn("One or more required users or tweets for replies not found. Reply seeding will be incomplete.");
            return;
        }

        createReplyIfNotExists(bob, tweet1_alice_spring, null, "Congrats Alice! Spring Boot is awesome. I'm curious, what database are you using with it?");
        Reply reply_bob_to_alice_t1 = findReplyByContentStart("Congrats Alice! Spring Boot is awesome.");

        if (reply_bob_to_alice_t1 != null) {
            createReplyIfNotExists(alice, tweet1_alice_spring, reply_bob_to_alice_t1.getId(), "Thanks Bob! I'm using PostgreSQL for this project. It's been great so far!");
            Reply nestedReplyAliceToBob = findReplyByContentStart("Thanks Bob! I'm using PostgreSQL");
            if (nestedReplyAliceToBob != null) {
                createReplyIfNotExists(charlie, tweet1_alice_spring, nestedReplyAliceToBob.getId(), "PostgreSQL is a solid choice! Have you looked into any ORMs for it?");
                Reply nestedReplyCharlieToAlice = findReplyByContentStart("PostgreSQL is a solid choice!");
                if (nestedReplyCharlieToAlice != null) {
                    createReplyIfNotExists(alice, tweet1_alice_spring, nestedReplyCharlieToAlice.getId(), "Yes, using Spring Data JPA. Works like a charm!");
                }
            }
            createReplyIfNotExists(ethan, tweet1_alice_spring, reply_bob_to_alice_t1.getId(), "Impressive stuff, Alice!");
        }

        createReplyIfNotExists(diana, tweet2_bob_bangkok, null, "You HAVE to try Mango Sticky Rice if you haven't already! It's divine. Enjoy Bangkok!");
        createReplyIfNotExists(ethan, tweet2_bob_bangkok, null, "Looks amazing! I was there last year, the food is unforgettable. Pad Thai from the street vendors is a must.");
        Reply reply_ethan_to_bob_t2 = findReplyByContentStart("Looks amazing! I was there last year");
        if (reply_ethan_to_bob_t2 != null) {
            createReplyIfNotExists(bob, tweet2_bob_bangkok, reply_ethan_to_bob_t2.getId(), "Thanks for the tip, Ethan! Will definitely try that.");
        }


        createReplyIfNotExists(charlie, tweet3_alice_react, null, "Nice! I've been meaning to dive deeper into TypeScript with React. Any good resources you'd recommend?");
        Reply reply_charlie_to_alice_t3 = findReplyByContentStart("Nice! I've been meaning to dive deeper");

        if (reply_charlie_to_alice_t3 != null) {
            createReplyIfNotExists(alice, tweet3_alice_react, reply_charlie_to_alice_t3.getId(), "Definitely check out the official React and TypeScript docs. Also, 'Fullstack TypeScript' by Mike North is a great book!");
        }

        createReplyIfNotExists(bob, tweet5_diana_book, null, "Oh, 'Project Hail Mary' is on my list! Glad to hear it's good. I'm currently reading 'The Martian' by him.");
        createReplyIfNotExists(alice, tweet5_diana_book, null, "Seconded! 'Project Hail Mary' was fantastic. The science was so well done.");
        createReplyIfNotExists(george, tweet5_diana_book, null, "Added to my backlog after this Valorant session! Sounds cool.");


        createReplyIfNotExists(alice, tweet7_ethan_fuji, null, "Wow, incredible view Ethan! Must have been a tough climb.");
        createReplyIfNotExists(fiona, tweet8_fiona_yoga, null, "That's the spirit, Fiona! Keep inspiring us.");
        createReplyIfNotExists(alice, tweet_george_valorant, null, "Nice win George! Which agent were you playing?");


        logger.info("{} total replies in DB after seeding attempt.", replyRepositoryJpa.count());
    }

    private void createReplyIfNotExists(User author, Tweet targetTweet, Long parentReplyId, String content) throws EntityNotFoundException {
        boolean replyExists = replyRepositoryJpa.findAll().stream()
                .anyMatch(r -> r.getUser().getId().equals(author.getId()) &&
                        r.getTweet().getId().equals(targetTweet.getId()) &&
                        (parentReplyId == null ? r.getParentReply() == null : (r.getParentReply() != null && r.getParentReply().getId().equals(parentReplyId))) &&
                        r.getContent().equals(content));

        if (!replyExists) {
            logger.info("Creating reply: \"{}...\" by {}", content.substring(0, Math.min(30, content.length())), author.getNickname());
            Reply replyEntity = Reply.builder().content(content).build();
            DisplayReply displayReply = replyService.createReplyToTweet(replyEntity, author.getId(), targetTweet.getId(), parentReplyId);
            createdReplies.add(displayReply.getReply()); // Assuming DisplayReply has getReply()
        } else {
            logger.info("Reply already exists: \"{}...\" by {}", content.substring(0, Math.min(30, content.length())), author.getNickname());
            replyRepositoryJpa.findAll().stream()
                    .filter(r -> r.getUser().getId().equals(author.getId()) &&
                            r.getTweet().getId().equals(targetTweet.getId()) &&
                            (parentReplyId == null ? r.getParentReply() == null : (r.getParentReply() != null && r.getParentReply().getId().equals(parentReplyId))) &&
                            r.getContent().equals(content))
                    .findFirst().ifPresent(createdReplies::add);
        }
    }

    private Reply findReplyByContentStart(String contentStart) {
        Optional<Reply> inMemoryReply = createdReplies.stream()
                .filter(r -> r.getContent().startsWith(contentStart))
                .findFirst();
        if (inMemoryReply.isPresent()) {
            return inMemoryReply.get();
        }
        Optional<Reply> dbReply = replyRepositoryJpa.findAll().stream()
                .filter(r -> r.getContent().startsWith(contentStart))
                .findFirst();
        dbReply.ifPresent(createdReplies::add);
        return dbReply.orElse(null);
    }


    private void seedFollowers() throws EntityNotFoundException, ReactionAlreadyExistsException {
        logger.info("Seeding followers...");
        if (createdUsers.size() < 2) {
            logger.info("Not enough users to create follow relationships. Attempting to load from DB.");
            createdUsers.clear();
            createdUsers.addAll(userRepositoryJpa.findAll());
            if(createdUsers.size() < 2) {
                logger.warn("Still not enough users. Skipping follower seeding.");
                return;
            }
        }

        User alice = findUserByNickname("AliceDev");
        User bob = findUserByNickname("FoodieBob");
        User charlie = findUserByNickname("CreativeCharlie");
        User diana = findUserByNickname("BookwormDiana");
        User ethan = findUserByNickname("EthanTravels");
        User fiona = findUserByNickname("FitFiona");
        User george = findUserByNickname("GamerGeorge");
        User hannah = findUserByNickname("CraftyHannah");

        if (alice == null || bob == null || charlie == null || diana == null || ethan == null || fiona == null || george == null || hannah == null) {
            logger.warn("One or more required users not found for following. Follower seeding will be incomplete.");
            return;
        }

        followIfNotFollowing(alice, bob); followIfNotFollowing(alice, charlie); followIfNotFollowing(alice, fiona); followIfNotFollowing(alice, ethan);
        followIfNotFollowing(bob, alice); followIfNotFollowing(bob, diana); followIfNotFollowing(bob, ethan); followIfNotFollowing(bob, hannah);
        followIfNotFollowing(charlie, diana); followIfNotFollowing(charlie, george); followIfNotFollowing(charlie, alice);
        followIfNotFollowing(diana, alice); followIfNotFollowing(diana, bob); followIfNotFollowing(diana, hannah); followIfNotFollowing(diana, charlie);
        followIfNotFollowing(ethan, fiona); followIfNotFollowing(ethan, bob); followIfNotFollowing(ethan, alice);
        followIfNotFollowing(fiona, ethan); followIfNotFollowing(fiona, alice); followIfNotFollowing(fiona, george);
        followIfNotFollowing(george, charlie); followIfNotFollowing(george, alice);
        followIfNotFollowing(hannah, diana); followIfNotFollowing(hannah, bob);

        logger.info("Follower relationships processed.");
    }

    private void followIfNotFollowing(User follower, User userToFollow) throws EntityNotFoundException, ReactionAlreadyExistsException {
        Optional<Follower> existingFollow = followerRepositoryJpa.findByFollowerAndUser(follower.getId(), userToFollow.getId());
        if (existingFollow.isEmpty()) {
            logger.info("{} following {}", follower.getNickname(), userToFollow.getNickname());
            userService.followUser(follower.getId(), userToFollow.getId());
        } else {
            logger.info("{} already follows {}", follower.getNickname(), userToFollow.getNickname());
        }
    }

    private void seedLikes() throws EntityNotFoundException, ReactionAlreadyExistsException {
        logger.info("Seeding likes...");
        if (createdUsers.isEmpty() || (createdTweets.isEmpty() && createdReplies.isEmpty())) {
            logger.info("No users, tweets, or replies available for liking. Attempting to load from DB.");
            if(createdUsers.isEmpty()) createdUsers.addAll(userRepositoryJpa.findAll());
            if(createdTweets.isEmpty()) createdTweets.addAll(tweetRepositoryJpa.findAll());
            if(createdReplies.isEmpty()) createdReplies.addAll(replyRepositoryJpa.findAll());

            if(createdUsers.isEmpty() || (createdTweets.isEmpty() && createdReplies.isEmpty())){
                logger.warn("Still not enough data. Skipping like seeding.");
                return;
            }
        }

        User alice = findUserByNickname("AliceDev");
        User bob = findUserByNickname("FoodieBob");
        User charlie = findUserByNickname("CreativeCharlie");
        User diana = findUserByNickname("BookwormDiana");
        User ethan = findUserByNickname("EthanTravels");
        User fiona = findUserByNickname("FitFiona");
        User george = findUserByNickname("GamerGeorge");
        User hannah = findUserByNickname("CraftyHannah");


        Tweet tweet1_alice = findTweetByContentStart("Just deployed my new Spring Boot app!");
        Tweet tweet2_bob = findTweetByContentStart("Exploring the street food scene in Bangkok!");
        Tweet tweet3_alice_react = findTweetByContentStart("Working on a new React component with TypeScript.");
        Tweet tweet4_charlie_art = findTweetByContentStart("Spent the morning sketching by the lake.");
        Tweet tweet5_diana_book = findTweetByContentStart("Just finished reading 'Project Hail Mary'");
        Tweet tweet6_bob_rome = findTweetByContentStart("Found the most amazing hidden gem for pasta in Rome.");
        Tweet tweet7_ethan_fuji = findTweetByContentStart("Reached the summit of Mount Fuji today!");
        Tweet tweet8_fiona_yoga = findTweetByContentStart("Morning yoga session done!");
        Tweet tweet9_george_valorant = findTweetByContentStart("Just won an intense match in Valorant!");
        Tweet tweet10_hannah_scarf = findTweetByContentStart("Finished knitting this cozy scarf.");


        Reply reply_bob_to_alice_t1 = findReplyByContentStart("Congrats Alice! Spring Boot is awesome.");
        Reply reply_alice_to_bob_r1_nested = findReplyByContentStart("Thanks Bob! I'm using PostgreSQL");
        Reply reply_charlie_to_alice_r2_nested = findReplyByContentStart("PostgreSQL is a solid choice!");
        Reply reply_diana_to_bob_t2 = findReplyByContentStart("You HAVE to try Mango Sticky Rice");
        Reply reply_ethan_to_bob_t2 = findReplyByContentStart("Looks amazing! I was there last year");


        if (alice == null || bob == null || charlie == null || diana == null || ethan == null || fiona == null || george == null || hannah == null) {
            logger.warn("One or more users for liking not found. Like seeding incomplete.");
            return;
        }

        // Tweet Likes
        if (tweet1_alice != null) { likeTweetIfNotLiked(bob, tweet1_alice); likeTweetIfNotLiked(charlie, tweet1_alice); likeTweetIfNotLiked(diana, tweet1_alice); likeTweetIfNotLiked(ethan, tweet1_alice); }
        if (tweet2_bob != null) { likeTweetIfNotLiked(alice, tweet2_bob); likeTweetIfNotLiked(ethan, tweet2_bob); likeTweetIfNotLiked(fiona, tweet2_bob); }
        if (tweet3_alice_react != null) { likeTweetIfNotLiked(fiona, tweet3_alice_react); likeTweetIfNotLiked(bob, tweet3_alice_react); }
        if (tweet4_charlie_art != null) { likeTweetIfNotLiked(alice, tweet4_charlie_art); likeTweetIfNotLiked(diana, tweet4_charlie_art); }
        if (tweet5_diana_book != null) { likeTweetIfNotLiked(bob, tweet5_diana_book); likeTweetIfNotLiked(alice, tweet5_diana_book); likeTweetIfNotLiked(george, tweet5_diana_book); }
        if (tweet6_bob_rome != null) { likeTweetIfNotLiked(alice, tweet6_bob_rome); likeTweetIfNotLiked(diana, tweet6_bob_rome); }
        if (tweet7_ethan_fuji != null) { likeTweetIfNotLiked(fiona, tweet7_ethan_fuji); likeTweetIfNotLiked(bob, tweet7_ethan_fuji); likeTweetIfNotLiked(alice, tweet7_ethan_fuji); }
        if (tweet8_fiona_yoga != null) { likeTweetIfNotLiked(ethan, tweet8_fiona_yoga); likeTweetIfNotLiked(hannah, tweet8_fiona_yoga); }
        if (tweet9_george_valorant != null) { likeTweetIfNotLiked(alice, tweet9_george_valorant); likeTweetIfNotLiked(charlie, tweet9_george_valorant); }
        if (tweet10_hannah_scarf != null) { likeTweetIfNotLiked(diana, tweet10_hannah_scarf); likeTweetIfNotLiked(fiona, tweet10_hannah_scarf); }


        // Reply Likes
        if (reply_bob_to_alice_t1 != null) { likeReplyIfNotLiked(alice, reply_bob_to_alice_t1); likeReplyIfNotLiked(charlie, reply_bob_to_alice_t1); }
        if (reply_alice_to_bob_r1_nested != null) { likeReplyIfNotLiked(bob, reply_alice_to_bob_r1_nested); likeReplyIfNotLiked(charlie, reply_alice_to_bob_r1_nested); }
        if (reply_charlie_to_alice_r2_nested != null) { likeReplyIfNotLiked(alice, reply_charlie_to_alice_r2_nested); likeReplyIfNotLiked(bob, reply_charlie_to_alice_r2_nested); }
        if (reply_diana_to_bob_t2 != null) { likeReplyIfNotLiked(bob, reply_diana_to_bob_t2); likeReplyIfNotLiked(alice, reply_diana_to_bob_t2); }
        if (reply_ethan_to_bob_t2 != null) { likeReplyIfNotLiked(bob, reply_ethan_to_bob_t2); }


        logger.info("Likes processed.");
    }

    private void likeTweetIfNotLiked(User user, Tweet tweet) throws EntityNotFoundException, ReactionAlreadyExistsException {
        if (tweet == null || user == null) { logger.warn("Cannot like null tweet or by null user."); return; }
        if (likeTweetRepositoryJpa.findLikeByUserIdAndTweetId(user.getId(), tweet.getId()).isEmpty()) {
            logger.info("{} liking tweet: \"{}...\"", user.getNickname(), tweet.getContent().substring(0, Math.min(20, tweet.getContent().length())));
            tweetService.createLikeToTweet(new LikeTweet(), user.getId(), tweet.getId());
        } else {
            logger.info("{} already liked tweet: \"{}...\"", user.getNickname(), tweet.getContent().substring(0, Math.min(20, tweet.getContent().length())));
        }
    }

    private void likeReplyIfNotLiked(User user, Reply reply) throws EntityNotFoundException, ReactionAlreadyExistsException {
        if (reply == null || user == null) { logger.warn("Cannot like null reply or by null user."); return; }
        if (likeReplyRepositoryJpa.findLikeByUserIdAndReplyId(user.getId(), reply.getId()).isEmpty()) {
            logger.info("{} liking reply: \"{}...\"", user.getNickname(), reply.getContent().substring(0, Math.min(20, reply.getContent().length())));
            replyService.createLikeToReply(new LikeReply(), user.getId(), reply.getId());
        } else {
            logger.info("{} already liked reply: \"{}...\"", user.getNickname(), reply.getContent().substring(0, Math.min(20, reply.getContent().length())));
        }
    }

    private void seedBookmarks() throws EntityNotFoundException, ReactionAlreadyExistsException {
        logger.info("Seeding bookmarks...");
        if (createdUsers.isEmpty() || createdTweets.isEmpty()) {
            logger.info("No users or tweets available for bookmarking. Attempting to load from DB.");
            if(createdUsers.isEmpty()) createdUsers.addAll(userRepositoryJpa.findAll());
            if(createdTweets.isEmpty()) createdTweets.addAll(tweetRepositoryJpa.findAll());
            if(createdUsers.isEmpty() || createdTweets.isEmpty()){
                logger.warn("Still not enough data. Skipping bookmark seeding.");
                return;
            }
        }

        User alice = findUserByNickname("AliceDev");
        User diana = findUserByNickname("BookwormDiana");
        User bob = findUserByNickname("FoodieBob");
        User fiona = findUserByNickname("FitFiona");
        User ethan = findUserByNickname("EthanTravels");
        User george = findUserByNickname("GamerGeorge");


        Tweet tweet1_alice_spring = findTweetByContentStart("Just deployed my new Spring Boot app!");
        Tweet tweet2_bob_bangkok = findTweetByContentStart("Exploring the street food scene in Bangkok!");
        Tweet tweet4_charlie_art = findTweetByContentStart("Spent the morning sketching by the lake.");
        Tweet tweet7_ethan_fuji = findTweetByContentStart("Reached the summit of Mount Fuji today!");
        Tweet tweet9_george_valorant = findTweetByContentStart("Just won an intense match in Valorant!");


        if (alice == null || diana == null || bob == null || fiona == null || ethan == null || george == null) {
            logger.warn("One or more users for bookmarking not found. Bookmark seeding incomplete.");
            return;
        }

        if (tweet2_bob_bangkok != null) bookmarkTweetIfNotBookmarked(alice, tweet2_bob_bangkok);
        if (tweet4_charlie_art != null) {
            bookmarkTweetIfNotBookmarked(diana, tweet4_charlie_art);
            bookmarkTweetIfNotBookmarked(alice, tweet4_charlie_art);
        }
        if (tweet7_ethan_fuji != null) {
            bookmarkTweetIfNotBookmarked(bob, tweet7_ethan_fuji);
            bookmarkTweetIfNotBookmarked(fiona, tweet7_ethan_fuji);
        }
        if (tweet1_alice_spring != null) {
            bookmarkTweetIfNotBookmarked(fiona, tweet1_alice_spring);
            bookmarkTweetIfNotBookmarked(ethan, tweet1_alice_spring);
        }
        if (tweet9_george_valorant != null) bookmarkTweetIfNotBookmarked(diana, tweet9_george_valorant);


        logger.info("Bookmarks processed.");
    }

    private void bookmarkTweetIfNotBookmarked(User user, Tweet tweet) throws EntityNotFoundException, ReactionAlreadyExistsException {
        if (tweet == null || user == null) { logger.warn("Cannot bookmark null tweet or by null user."); return; }
        if (bookmarkRepositoryJpa.findBookmarkByUserIdAndTweetId(user.getId(), tweet.getId()).isEmpty()) {
            logger.info("{} bookmarking tweet: \"{}...\"", user.getNickname(), tweet.getContent().substring(0, Math.min(20, tweet.getContent().length())));
            tweetService.createBookmarkToTweet(new Bookmark(), user.getId(), tweet.getId());
        } else {
            logger.info("{} already bookmarked tweet: \"{}...\"", user.getNickname(), tweet.getContent().substring(0, Math.min(20, tweet.getContent().length())));
        }
    }
}