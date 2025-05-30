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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tweets", description = "Endpoints for managing tweets")
@SecurityRequirement(name = "bearerAuth")
public class TweetController {

    private final TweetService tweetService;
    private final JwtService jwtService;

    @Operation(summary = "Create a new tweet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tweet created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateTweetDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User ID in DTO does not match token"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
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
            createTweetDto.setId(savedTweet.getId());
            return ResponseEntity.created(location).body(createTweetDto);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get trending tweets (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved trending tweets",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User ID not extractable from token",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "User not found (for context)",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @GetMapping("/trending")
    public ResponseEntity<?> getTrendingTweets(
            @Parameter(description = "Page number, 0-indexed") @RequestParam(defaultValue = "0") String page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") String size,
            HttpServletRequest request
    ) {
        String userIdStr = extractUserIdFromToken(request);
        if (userIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User ID not extractable from token");
        }
        long userId = parseInt(userIdStr);
        Pageable pageable = PageRequest.of(parseInt(page), parseInt(size));
        try {
            Page<DisplayTweet> tweets = tweetService.getTrendingTweets(pageable, userId);
            return ResponseEntity.ok(tweets.map(DisplayTweetDto::new));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get tweets from users followed by the current user (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tweets from followed users",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User ID not extractable from token",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "User not found (for context)",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @GetMapping("/followed")
    public ResponseEntity<?> getTweetsByFollowedUsers(
            @Parameter(description = "Page number, 0-indexed") @RequestParam(defaultValue = "0") String page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "20") String size,
            HttpServletRequest request
    ) {
        String userIdStr = extractUserIdFromToken(request);
        if (userIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User ID not extractable from token");
        }
        long userId = parseInt(userIdStr);
        Pageable pageable = PageRequest.of(parseInt(page), parseInt(size));
        try {
            Page<DisplayTweet> tweets = tweetService.getTrendingTweetsByFollowedUsers(pageable, userId);
            return ResponseEntity.ok(tweets.map(DisplayTweetDto::new));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Find a tweet by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tweet found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DisplayTweetDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User ID not extractable from token",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Tweet not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> findTweetById(
            @Parameter(description = "ID of the tweet to retrieve") @PathVariable long id,
            HttpServletRequest request) {
        String userIdStr = extractUserIdFromToken(request);
        if (userIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User ID not extractable from token");
        }
        long userId = parseInt(userIdStr);
        try {
            DisplayTweet tweet = tweetService.findTweetById(id, userId);
            return ResponseEntity.ok(new DisplayTweetDto(tweet));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get tweets by a specific user (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user's tweets",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User ID not extractable from token",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @GetMapping("/{userId}/user")
    ResponseEntity<?> getTweetByUserId(
            @Parameter(description = "ID of the user whose tweets to retrieve") @PathVariable long userId,
            @Parameter(description = "Page number, 0-indexed") @RequestParam(defaultValue = "0") String page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") String size,
            HttpServletRequest request
    ) {
        String currentUserIdStr = extractUserIdFromToken(request);
        if (currentUserIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User ID not extractable from token");
        }
        long currentUserId = parseInt(currentUserIdStr);
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

    @Operation(summary = "Get tweets by a specific hashtag (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tweets for the hashtag",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User ID not found in token",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Hashtag or User not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @GetMapping("/{hashtagId}/hashtag")
    public ResponseEntity<?> getTweetsByHashtag(
            @Parameter(description = "ID of the hashtag") @PathVariable long hashtagId,
            @Parameter(description = "Page number, 0-indexed") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
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

    @Operation(summary = "Get bookmarked tweets for the current user (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved bookmarked tweets",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User ID in path does not match token"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @GetMapping("/{userId}/bookmarks")
    public ResponseEntity<?> getBookmarkedTweets(
            @Parameter(description = "ID of the user (must match authenticated user)") @PathVariable long userId,
            @Parameter(description = "Page number, 0-indexed") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
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

    @Operation(summary = "Like a tweet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tweet liked successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DisplayTweetDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User ID in DTO does not match token"),
            @ApiResponse(responseCode = "404", description = "User or Tweet not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "409", description = "Conflict - Tweet already liked by this user",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
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

    @Operation(summary = "Remove a like from a tweet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Like removed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DisplayTweetDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User ID not extractable from token",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to remove this like",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Like or Tweet not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> removeLikeToTweet(
            @Parameter(description = "ID of the like to remove") @PathVariable long id,
            HttpServletRequest request) {
        String userIdStr = extractUserIdFromToken(request);
        if (userIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User ID not extractable from token");
        }
        long userId = Long.parseLong(userIdStr);
        try {
            DisplayTweet tweet = tweetService.deleteLikeFromTweet(id, userId);
            return ResponseEntity.ok(new DisplayTweetDto(tweet));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Bookmark a tweet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tweet bookmarked successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DisplayTweetDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User ID in DTO does not match token"),
            @ApiResponse(responseCode = "404", description = "User or Tweet not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "409", description = "Conflict - Tweet already bookmarked by this user",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
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

    @Operation(summary = "Remove a bookmark from a tweet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bookmark removed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DisplayTweetDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User ID not extractable from token",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to remove this bookmark",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Bookmark or Tweet not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @DeleteMapping("/{id}/bookmark")
    public ResponseEntity<?> removeBookmarkToTweet(
            @Parameter(description = "ID of the bookmark to remove") @PathVariable long id,
            HttpServletRequest request) {
        String userIdStr = extractUserIdFromToken(request);
        if (userIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User ID not extractable from token");
        }
        long userId = Long.parseLong(userIdStr);
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