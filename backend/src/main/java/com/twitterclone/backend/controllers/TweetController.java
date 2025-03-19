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
import org.springframework.beans.factory.annotation.Autowired;
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


@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RestController
@RequestMapping("/api/tweet")
public class TweetController {

    private TweetService tweetService;
    private JwtService jwtService;

    @Autowired
    public TweetController(TweetService tweetService, JwtService jwtService) {
        this.tweetService = tweetService;
        this.jwtService = jwtService;
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

    @PostMapping
    public ResponseEntity<?> createTweet(@RequestBody CreateTweetDto createTweetDto,
                                         UriComponentsBuilder uriBuilder,
                                         HttpServletRequest request) {
        long userId = createTweetDto.getUserId();

        if (!isValidUser(request, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Tweet tweet = CreateTweetDto.fromDto(createTweetDto);
        try {
            Tweet savedTweet = tweetService.createTweet(tweet, userId);
            URI location = uriBuilder.path("/tweet/{id}").buildAndExpand(savedTweet.getId()).toUri();
            createTweetDto.setId(savedTweet.getUser().getId());
            return ResponseEntity.created(location).body(createTweetDto);
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

    @GetMapping("/{userId}/trending")
    public ResponseEntity<Page<DisplayTweetDto>> getTrendingTweets(
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "20") String size,
            @PathVariable long userId,
            HttpServletRequest request) {

        if (!isValidUser(request, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Pageable pageable = PageRequest.of(parseInt(page), parseInt(size));
        Page<DisplayTweet> tweets = tweetService.getTrendingTweets(pageable, userId);
        return ResponseEntity.ok(tweets.map(DisplayTweetDto::new));
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
}
