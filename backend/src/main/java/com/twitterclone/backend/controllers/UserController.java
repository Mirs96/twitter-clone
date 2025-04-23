package com.twitterclone.backend.controllers;

import com.twitterclone.backend.dto.*;
import com.twitterclone.backend.model.UserProfile;
import com.twitterclone.backend.model.entities.User;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.exceptions.ReactionAlreadyExistsException;
import com.twitterclone.backend.model.services.JwtService;
import com.twitterclone.backend.model.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins="http://localhost:4200", allowedHeaders = "*")
@RestController
@RequestMapping("/api/user")
public class UserController {
    private UserService userService;
    private JwtService jwtService;

    @Autowired
    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable long id, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);

        // Extract id from token
        String tokenUserId = jwtService.extractClaim(token, claims -> claims.get("userId", String.class));

        // Compare the id given by the request to the one extracted from the token
        if (!tokenUserId.equals(String.valueOf(id))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<User> oUser = userService.findById(id);
        return oUser.map(user -> ResponseEntity.ok(new UserDto(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}/profile")
    public ResponseEntity<?> getProfile(@PathVariable long userId, HttpServletRequest request) {
        String tokenUserId = extractUserIdFromToken(request);
        long currentUserId = Integer.parseInt(tokenUserId);

        try {
            UserProfile userProfile = userService.getProfile(userId, currentUserId);
            return ResponseEntity.ok(new UserProfileDto(userProfile));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{userId}/followers")
    ResponseEntity<?> findFollowersByUserId(
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "10") String size
    ) {
        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));

        try {
            Page<FollowerUserDto> followers = userService
                    .findFollowersByUserId(userId, pageable)
                    .map(FollowerUserDto::new);

            return ResponseEntity.ok(followers);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{followerId}/followed")
    ResponseEntity<?> findFollowingByFollowerId(
            @PathVariable long followerId,
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "10") String size
    ) {
        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));

        try {
            List<FollowedUserDto> followed = userService
                    .findFollowingByFollowerId(followerId, pageable)
                    .stream()
                    .map(FollowedUserDto::new)
                    .toList();

            return ResponseEntity.ok(followed);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/follow/{userIdToFollow}")
    public ResponseEntity<?> followUser(
            @PathVariable long userIdToFollow,
            HttpServletRequest request
    ) {
        String tokenUserId = extractUserIdFromToken(request);
        long followerId = Long.parseLong(tokenUserId);

        try {
            userService.followUser(followerId, userIdToFollow);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (ReactionAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/unfollow/{userIdToUnfollow}")
    public ResponseEntity<?> unfollowUser(
            @PathVariable long userIdToUnfollow,
            HttpServletRequest request
    ) {
        String tokenUserId = extractUserIdFromToken(request);
        long followerId = Long.parseLong(tokenUserId);

        try {
            userService.unfollowUser(followerId, userIdToUnfollow);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
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
}
