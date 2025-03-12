package com.twitterclone.backend.controllers;

import com.twitterclone.backend.dto.LikeTweetDto;
import com.twitterclone.backend.model.entities.LikeTweet;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.services.JwtService;
import com.twitterclone.backend.model.services.LikeTweetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins="http://localhost:4200", allowedHeaders = "*")
@RestController
@RequestMapping("/api/like-tweet")
public class LikeTweetController {
    private LikeTweetService likeTweetService;
    private JwtService jwtService;

    @Autowired
    public LikeTweetController(LikeTweetService likeTweetService, JwtService jwtService) {
        this.likeTweetService = likeTweetService;
        this.jwtService = jwtService;
    }

    @PostMapping
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
            likeTweetService.createLikeToTweet(like, likeDto.getUserId(), likeDto.getTweetId());

            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
