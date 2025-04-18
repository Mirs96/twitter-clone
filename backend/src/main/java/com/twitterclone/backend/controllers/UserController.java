package com.twitterclone.backend.controllers;

import com.twitterclone.backend.dto.*;
import com.twitterclone.backend.model.entities.User;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
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

    @GetMapping("/{userId}")
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

    @GetMapping("/{userId}/followers-count")
    public ResponseEntity<?> countFollowersByUserId(@PathVariable long userId) {
        try {
            long count = userService.countFollowersByUserId(userId);
            return ResponseEntity.ok(count);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{userId}/followed-count")
    public ResponseEntity<?> countFollowingByFollowerId(@PathVariable long followerId) {
        try {
            long count = userService.countFollowingByFollowerId(followerId);
            return ResponseEntity.ok(count);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
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

    @GetMapping("/{userId}/followed")
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
}
