package com.twitterclone.backend.controllers;

import com.twitterclone.backend.dto.*;
import com.twitterclone.backend.model.DisplayTweet;
import com.twitterclone.backend.model.entities.Bookmark;
import com.twitterclone.backend.model.entities.LikeTweet;
import com.twitterclone.backend.model.entities.Tweet;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.exceptions.ReactionAlreadyExistsException;
import com.twitterclone.backend.model.exceptions.UnauthorizedException;
import com.twitterclone.backend.model.services.JwtService;
import com.twitterclone.backend.model.services.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;

import static java.lang.Integer.parseInt;

@CrossOrigin(origins={"http://localhost:4200", "http://localhost:5173"}, allowedHeaders = "*")
@RestController
@RequestMapping("/api/tweet")
@RequiredArgsConstructor
public class TweetController {

    private final TweetService tweetService;
    private final JwtService jwtService;


    @PostMapping
    public ResponseEntity<?> createTweet(
            @RequestBody CreateTweetDto createTweetDto,
            UriComponentsBuilder uriBuilder,
            HttpServletRequest request
    ) {
        long userId = createTweetDto.getUserId();

        if (!isValidUser(request, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Tweet tweet = CreateTweetDto.fromDto(createTweetDto);
        try {
            Tweet savedTweet = tweetService.createTweet(tweet, userId);
            URI location = uriBuilder.path("/tweet/{id}").buildAndExpand(savedTweet.getId()).toUri();
            createTweetDto.setId(savedTweet.getId()); // Return created tweet id
            // Consider returning a DisplayTweetDto of the created tweet
            return ResponseEntity.created(location).body(createTweetDto);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/trending")
    public ResponseEntity<?> getTrendingTweets(
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "20") String size,
            HttpServletRequest request
    ) {
        long userId = parseInt(extractUserIdFromToken(request));
        Pageable pageable = PageRequest.of(parseInt(page), parseInt(size));

        try {
            Page<DisplayTweet> tweets = tweetService.getTrendingTweets(pageable, userId);
            return ResponseEntity.ok(tweets.map(DisplayTweetDto::new));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/followed")
    public ResponseEntity<?> getTweetsByFollowedUsers(
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "20") String size,
            HttpServletRequest request
    ) {
        long userId = parseInt(extractUserIdFromToken(request));
        Pageable pageable = PageRequest.of(parseInt(page), parseInt(size));

        try {
            Page<DisplayTweet> tweets = tweetService.getTrendingTweetsByFollowedUsers(pageable, userId);
            return ResponseEntity.ok(tweets.map(DisplayTweetDto::new));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findTweetById(@PathVariable long id, HttpServletRequest request) {
        long userId = parseInt(extractUserIdFromToken(request));
        try {
            DisplayTweet tweet = tweetService.findTweetById(id, userId);
            return ResponseEntity.ok(new DisplayTweetDto(tweet));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{userId}/user")
    ResponseEntity<?> getTweetByUserId(
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "10") String size,
            HttpServletRequest request
    ) {
        long currentUserId = parseInt(extractUserIdFromToken(request));

        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
        try {
            Page<DisplayTweetDto> tweets = tweetService
                    .getTweetByUserId(userId, currentUserId, pageable)
                    .map(DisplayTweetDto::new);

            return ResponseEntity.ok(tweets);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{hashtagId}/hashtag")
    public ResponseEntity<?> getTweetsByHashtag(
            @PathVariable long hashtagId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        String userIdFromToken = extractUserIdFromToken(request);
        if (userIdFromToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User ID not found in token");
        }
        long currentUserId = parseInt(userIdFromToken);
        Pageable pageable = PageRequest.of(page, size);
        try {
            Page<DisplayTweet> tweets = tweetService.getTweetsByHashtagId(hashtagId, pageable, currentUserId);
            Page<DisplayTweetDto> tweetDtos = tweets.map(DisplayTweetDto::new);
            return ResponseEntity.ok(tweetDtos);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{userId}/bookmarks")
    public ResponseEntity<?> getBookmarkedTweets(
        @PathVariable long userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        HttpServletRequest request
    ) {
        if (!isValidUser(request, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Pageable pageable = PageRequest.of(page, size);
        try {
            Page<DisplayTweet> bookmarkedTweets = tweetService.getBookmarkedTweetsByUserId(userId, pageable);
            Page<DisplayTweetDto> bookmarkedTweetsDto = bookmarkedTweets.map(DisplayTweetDto::new);
            return ResponseEntity.ok(bookmarkedTweetsDto);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/like")
    public ResponseEntity<?> createLikeToTweet(@RequestBody LikeTweetDto likeDto, HttpServletRequest request) {
        long userId = likeDto.getUserId();

        if (!isValidUser(request, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        LikeTweet like = LikeTweetDto.fromDto(likeDto);
        try {
            DisplayTweet updatedTweet = tweetService.createLikeToTweet(like, userId, likeDto.getTweetId());
            return ResponseEntity.ok(new DisplayTweetDto(updatedTweet));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        } catch (ReactionAlreadyExistsException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> removeLikeToTweet(@PathVariable long id, HttpServletRequest request) {
        long userId = Long.parseLong(extractUserIdFromToken(request));

        try {
            DisplayTweet tweet = tweetService.deleteLikeFromTweet(id, userId);
            return ResponseEntity.ok(new DisplayTweetDto(tweet));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/bookmark")
    public ResponseEntity<?> createBookmarkToTweet(@RequestBody BookmarkDto bookmarkDto, HttpServletRequest request) {
        long userId = bookmarkDto.getUserId();

        if (!isValidUser(request, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Bookmark bookmark = BookmarkDto.fromDto(bookmarkDto);
        try {
            DisplayTweet updatedTweet = tweetService.createBookmarkToTweet(bookmark, userId, bookmarkDto.getTweetId());
            return ResponseEntity.ok(new DisplayTweetDto(updatedTweet));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        } catch (ReactionAlreadyExistsException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/{id}/bookmark")
    public ResponseEntity<?> removeBookmarkToTweet(@PathVariable long id, HttpServletRequest request) {
        long userId = Long.parseLong(extractUserIdFromToken(request));

        try {
            DisplayTweet tweet = tweetService.deleteBookmarkFromTweet(id, userId);
            return ResponseEntity.ok(new DisplayTweetDto(tweet));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.FORBIDDEN);
        }
    }

    private String extractUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return jwtService.extractClaim(token, claims -> claims.get("userId", String.class));
    }

    private boolean isValidUser(HttpServletRequest request, long userId) {
        String tokenUserId = extractUserIdFromToken(request);
        return tokenUserId != null && tokenUserId.equals(String.valueOf(userId));
    }
}
