//package com.twitterclone.backend.controllers;
//
//import com.twitterclone.backend.dto.*;
//import com.twitterclone.backend.model.UserProfile;
//import com.twitterclone.backend.model.entities.User;
//import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
//import com.twitterclone.backend.model.exceptions.ReactionAlreadyExistsException;
//import com.twitterclone.backend.model.services.JwtService;
//import com.twitterclone.backend.model.services.UserService;
//import lombok.RequiredArgsConstructor;
//import net.coobird.thumbnailator.Thumbnails;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.UUID;
//import org.apache.commons.io.FilenameUtils;
//
//import javax.imageio.ImageIO;
//import java.util.List;
//import java.util.Optional;
//
//@CrossOrigin(origins={"http://localhost:4200", "http://localhost:5173"}, allowedHeaders = "*")
//@RestController
//@RequestMapping("/api/user")
//@RequiredArgsConstructor
//public class UserController {
//    private final UserService userService;
//    private final JwtService jwtService;
//
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getUserById(@PathVariable long id, HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        String token = authHeader.substring(7);
//
//        String tokenUserId = jwtService.extractClaim(token, claims -> claims.get("userId", String.class));
//
//        if (!tokenUserId.equals(String.valueOf(id))) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        Optional<User> oUser = userService.findById(id);
//        return oUser.map(user -> ResponseEntity.ok(new UserDto(user)))
//                .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//
//    @GetMapping("/{userId}/profile")
//    public ResponseEntity<?> getProfile(@PathVariable long userId, HttpServletRequest request) {
//        String tokenUserId = extractUserIdFromToken(request);
//        long currentUserId = Integer.parseInt(tokenUserId);
//
//        try {
//            UserProfile userProfile = userService.getProfile(userId, currentUserId);
//            return ResponseEntity.ok(new UserProfileDto(userProfile));
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @PostMapping("/{userId}/update-profile")
//    public ResponseEntity<?> updateProfile(
//            @PathVariable long userId,
//            @RequestParam(value = "bio", required = false) String bio,
//            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
//            HttpServletRequest request
//    ) {
//        if (!isValidUser(request, userId)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
//
//        String filename = null;
//        try {
//            if (avatar != null && !avatar.isEmpty()) {
//                String ext = FilenameUtils.getExtension(avatar.getOriginalFilename());
//                filename = UUID.randomUUID() + "." + ext;
//
////                Path projectRootPath = Paths.get("").toAbsolutePath().getParent();
////                Path folder = projectRootPath.resolve("uploads/avatars");
//                Path basePhysicalUploadPath = Paths.get("C:/dev/twitter-clone/uploads/");
//                Path folder = basePhysicalUploadPath.resolve("avatars");
//
//
//                if (!Files.exists(folder)) {
//                    Files.createDirectories(folder);
//                }
//
//                BufferedImage img = ImageIO.read(avatar.getInputStream());
//
//                boolean needsCompression = (img.getWidth() > 800 || img.getHeight() > 800) || avatar.getSize() > (500 * 1024);
//
//                if (needsCompression) {
//                    Thumbnails.of(img)
//                            .size(800, 800)
//                            .outputQuality(0.6)
//                            .toFile(folder.resolve(filename).toFile());
//                } else {
//                    Files.copy(avatar.getInputStream(), folder.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
//                }
//            }
//
//            userService.updateUserProfile(userId, bio, filename);
//            return ResponseEntity.ok().build();
//        } catch (IOException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @GetMapping("/{userId}/followers")
//    ResponseEntity<?> findFollowersByUserId(
//            @PathVariable long userId,
//            @RequestParam(defaultValue = "0") String page,
//            @RequestParam(defaultValue = "10") String size
//    ) {
//        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
//
//        try {
//            Page<FollowerUserDto> followers = userService
//                    .findFollowersByUserId(userId, pageable)
//                    .map(FollowerUserDto::new);
//
//            return ResponseEntity.ok(followers);
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @GetMapping("/{followerId}/followed")
//    ResponseEntity<?> findFollowingByFollowerId(
//            @PathVariable long followerId,
//            @RequestParam(defaultValue = "0") String page,
//            @RequestParam(defaultValue = "10") String size
//    ) {
//        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
//
//        try {
//            List<FollowedUserDto> followed = userService
//                    .findFollowingByFollowerId(followerId, pageable)
//                    .stream()
//                    .map(FollowedUserDto::new)
//                    .toList();
//
//            return ResponseEntity.ok(followed);
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @PostMapping("/follow/{userIdToFollow}")
//    public ResponseEntity<?> followUser(
//            @PathVariable long userIdToFollow,
//            HttpServletRequest request
//    ) {
//        String tokenUserId = extractUserIdFromToken(request);
//        long followerId = Long.parseLong(tokenUserId);
//
//        try {
//            userService.followUser(followerId, userIdToFollow);
//            return ResponseEntity.noContent().build();
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//        } catch (ReactionAlreadyExistsException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }
//
//    @DeleteMapping("/unfollow/{userIdToUnfollow}")
//    public ResponseEntity<?> unfollowUser(
//            @PathVariable long userIdToUnfollow,
//            HttpServletRequest request
//    ) {
//        String tokenUserId = extractUserIdFromToken(request);
//        long followerId = Long.parseLong(tokenUserId);
//
//        try {
//            userService.unfollowUser(followerId, userIdToUnfollow);
//            return ResponseEntity.noContent().build();
//        } catch (EntityNotFoundException e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
//        }
//    }
//
//    private String extractUserIdFromToken(HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return null;
//        }
//        String token = authHeader.substring(7);
//        return jwtService.extractClaim(token, claims -> claims.get("userId", String.class));
//    }
//
//    private boolean isValidUser(HttpServletRequest request, long userId) {
//        String tokenUserId = extractUserIdFromToken(request);
//        return tokenUserId != null && tokenUserId.equals(String.valueOf(userId));
//    }
//}

// File: src/main/java/com/twitterclone/backend/controllers/UserController.java
package com.twitterclone.backend.controllers;

import com.twitterclone.backend.dto.*;
import com.twitterclone.backend.model.UserProfile;
import com.twitterclone.backend.model.entities.User;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.exceptions.ReactionAlreadyExistsException;
import com.twitterclone.backend.model.services.JwtService;
import com.twitterclone.backend.model.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins={"http://localhost:4200", "http://localhost:5173"}, allowedHeaders = "*")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for user profiles and interactions")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    @Operation(summary = "Get user details by ID (authenticated user only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Token user ID does not match path user ID", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @Parameter(description = "ID of the user to retrieve") @PathVariable long id,
            HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        String tokenUserId = jwtService.extractClaim(token, claims -> claims.get("userId", String.class));

        if (tokenUserId == null || !tokenUserId.equals(String.valueOf(id))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<User> oUser = userService.findById(id);
        return oUser.map(user -> ResponseEntity.ok(new UserDto(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get user profile by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User ID not extractable from token",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @GetMapping("/{userId}/profile")
    public ResponseEntity<?> getProfile(
            @Parameter(description = "ID of the user whose profile to retrieve") @PathVariable long userId,
            HttpServletRequest request) {
        String tokenUserId = extractUserIdFromToken(request);
        if (tokenUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User ID not extractable from token");
        }
        long currentUserId = Integer.parseInt(tokenUserId);

        try {
            UserProfile userProfile = userService.getProfile(userId, currentUserId);
            return ResponseEntity.ok(new UserProfileDto(userProfile));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Update user profile (bio and avatar)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - User ID in path does not match token", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Internal server error during file processing",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @PostMapping(value = "/{userId}/update-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            @Parameter(description = "ID of the user to update (must match authenticated user)") @PathVariable long userId,
            @Parameter(description = "New bio for the user") @RequestParam(value = "bio", required = false) String bio,
            @Parameter(description = "New avatar image file") @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            HttpServletRequest request
    ) {
        if (!isValidUser(request, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String filename = null;
        try {
            if (avatar != null && !avatar.isEmpty()) {
                String ext = FilenameUtils.getExtension(avatar.getOriginalFilename());
                filename = UUID.randomUUID() + "." + ext;
                Path basePhysicalUploadPath = Paths.get("C:/dev/twitter-clone/uploads/");
                Path folder = basePhysicalUploadPath.resolve("avatars");

                if (!Files.exists(folder)) {
                    Files.createDirectories(folder);
                }
                BufferedImage img = ImageIO.read(avatar.getInputStream());
                boolean needsCompression = (img.getWidth() > 800 || img.getHeight() > 800) || avatar.getSize() > (500 * 1024);

                if (needsCompression) {
                    Thumbnails.of(img)
                            .size(800, 800)
                            .outputQuality(0.6)
                            .toFile(folder.resolve(filename).toFile());
                } else {
                    Files.copy(avatar.getInputStream(), folder.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            userService.updateUserProfile(userId, bio, filename);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get followers of a user (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved followers",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @GetMapping("/{userId}/followers")
    ResponseEntity<?> findFollowersByUserId(
            @Parameter(description = "ID of the user whose followers to retrieve") @PathVariable long userId,
            @Parameter(description = "Page number, 0-indexed") @RequestParam(defaultValue = "0") String page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") String size
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

    @Operation(summary = "Get users followed by a specific user (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved followed users",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FollowedUserDto.class)))),
            @ApiResponse(responseCode = "404", description = "User (follower) not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @GetMapping("/{followerId}/followed")
    ResponseEntity<?> findFollowingByFollowerId(
            @Parameter(description = "ID of the user (follower) whose followed list to retrieve") @PathVariable long followerId,
            @Parameter(description = "Page number, 0-indexed") @RequestParam(defaultValue = "0") String page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") String size
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

    @Operation(summary = "Follow a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User followed successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Bad Request - Cannot follow oneself or already following",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User ID not extractable from token",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "User (follower or user to follow) not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @PostMapping("/follow/{userIdToFollow}")
    public ResponseEntity<?> followUser(
            @Parameter(description = "ID of the user to follow") @PathVariable long userIdToFollow,
            HttpServletRequest request
    ) {
        String tokenUserId = extractUserIdFromToken(request);
        if (tokenUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User ID not extractable from token");
        }
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

    @Operation(summary = "Unfollow a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User unfollowed successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User ID not extractable from token",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "User (follower or user to unfollow) or follow relationship not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @DeleteMapping("/unfollow/{userIdToUnfollow}")
    public ResponseEntity<?> unfollowUser(
            @Parameter(description = "ID of the user to unfollow") @PathVariable long userIdToUnfollow,
            HttpServletRequest request
    ) {
        String tokenUserId = extractUserIdFromToken(request);
        if (tokenUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User ID not extractable from token");
        }
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

    private boolean isValidUser(HttpServletRequest request, long userId) {
        String tokenUserId = extractUserIdFromToken(request);
        return tokenUserId != null && tokenUserId.equals(String.valueOf(userId));
    }
}
