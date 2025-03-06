package com.twitterclone.backend.controllers;

import com.twitterclone.backend.dto.TweetDto;
import com.twitterclone.backend.model.entities.Hashtag;
import com.twitterclone.backend.model.entities.Tweet;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.security.JwtAuthenticationFilter;
import com.twitterclone.backend.model.services.HashtagService;
import com.twitterclone.backend.model.services.JwtService;
import com.twitterclone.backend.model.services.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
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
            @RequestBody TweetDto tweetDto,
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
        if (!tokenUserId.equals(String.valueOf(tweetDto.getUserId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Tweet tweet = TweetDto.fromDto(tweetDto);
        try {
            Tweet savedTweet = tweetService.createTweet(tweet, tweetDto.getUserId());
            URI location = uriBuilder.path("/tweet/{id}").buildAndExpand(savedTweet.getId()).toUri();;
            tweetDto.setId(savedTweet.getUser().getId());
            return ResponseEntity.created(location).body(tweetDto);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
