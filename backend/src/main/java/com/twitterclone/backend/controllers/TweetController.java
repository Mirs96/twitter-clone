package com.twitterclone.backend.controllers;

import com.twitterclone.backend.dto.CreateTweetDto;
import com.twitterclone.backend.dto.DisplayTweetDto;
import com.twitterclone.backend.dto.LikeTweetDto;
import com.twitterclone.backend.model.DisplayTweet;
import com.twitterclone.backend.model.entities.LikeTweet;
import com.twitterclone.backend.model.entities.Tweet;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.exceptions.ReactionAlreadyExistsException;
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

@CrossOrigin(origins="http://localhost:4200", allowedHeaders = "*")
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

    @PostMapping
    public ResponseEntity<?> createTweet(
            @RequestBody CreateTweetDto createTweetDto,
            UriComponentsBuilder uriBuilder,
            HttpServletRequest request) {
        // Retrieve token by the header "Authorization"
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        // Extract id from token
        String tokenUserId = jwtService.extractClaim(token, claims -> claims.get("userId", String.class));
        // Compare the id given by the request to the one extracted from the token
        if (!tokenUserId.equals(String.valueOf(createTweetDto.getUserId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Tweet tweet = CreateTweetDto.fromDto(createTweetDto);
        try {
            Tweet savedTweet = tweetService.createTweet(tweet, createTweetDto.getUserId());
            URI location = uriBuilder.path("/tweet/{id}").buildAndExpand(savedTweet.getId()).toUri();;
            createTweetDto.setId(savedTweet.getUser().getId());
            return ResponseEntity.created(location).body(createTweetDto);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{userId}/trending")
    public ResponseEntity<Page<DisplayTweetDto>> getTrendingTweets(
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "20") String size,
            @PathVariable long userId,
            HttpServletRequest request
    ) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        // Extract id from token
        String tokenUserId = jwtService.extractClaim(token, claims -> claims.get("userId", String.class));
        // Compare the id given by the request to the one extracted from the token
        if (!tokenUserId.equals(String.valueOf(userId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
        Page<DisplayTweet> tweets = tweetService.getTrendingTweets(pageable, userId);
        return ResponseEntity.ok(tweets.map(DisplayTweetDto::new));
    }

    @PostMapping("/like")
    public ResponseEntity<?> createLikeToTweet(@RequestBody LikeTweetDto likeDto, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        // Extract id from token
        String tokenUserId = jwtService.extractClaim(token, claims -> claims.get("userId", String.class));
        // Compare the id given by the request to the one extracted from the token
        if (!tokenUserId.equals(String.valueOf(likeDto.getUserId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        LikeTweet like = LikeTweetDto.fromDto(likeDto);
        try {
            DisplayTweet updatedTweet = tweetService.createLikeToTweet(like, likeDto.getUserId(), likeDto.getTweetId());
            return ResponseEntity.ok(new DisplayTweetDto(updatedTweet));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        } catch (ReactionAlreadyExistsException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> removeLikeToTweet(@PathVariable long id, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        // Extract id from token
        long userId = Integer.parseInt(jwtService.extractClaim(token, claims -> claims.get("userId", String.class)));

        try {
            DisplayTweet tweet = tweetService.deleteLikeFromTweet(id, userId);
            return ResponseEntity.ok(new DisplayTweetDto(tweet));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
