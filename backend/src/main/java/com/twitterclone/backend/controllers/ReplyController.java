package com.twitterclone.backend.controllers;

import com.twitterclone.backend.dto.*;
import com.twitterclone.backend.model.DisplayReply;
import com.twitterclone.backend.model.entities.LikeReply;
import com.twitterclone.backend.model.entities.Reply;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.exceptions.ReactionAlreadyExistsException;
import com.twitterclone.backend.model.exceptions.UnauthorizedException;
import com.twitterclone.backend.model.services.JwtService;
import com.twitterclone.backend.model.services.ReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

@CrossOrigin(origins={"http://localhost:4200", "http://localhost:5173"}, allowedHeaders = "*")
@RestController
@RequestMapping("/api/reply")
@RequiredArgsConstructor
@Tag(name = "Replies", description = "Endpoints for managing replies to tweets")
@SecurityRequirement(name = "bearerAuth")
public class ReplyController {

    private final ReplyService replyService;
    private final JwtService jwtService;

    @Operation(summary = "Create a reply to a tweet or another reply")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reply created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DisplayReplyDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User ID in DTO does not match token or invalid token"),
            @ApiResponse(responseCode = "404", description = "Entity not found (User, Tweet, or Parent Reply)",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @PostMapping
    public ResponseEntity<?> createReplyToTweet(
            @RequestBody CreateReplyDto replyDto,
            HttpServletRequest request
    ) {
        long userId = replyDto.getUserId();
        if (!isValidUser(request, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Reply reply = CreateReplyDto.fromDto(replyDto);
        try {
            DisplayReply createdReply = replyService.createReplyToTweet(reply, userId, replyDto.getTweetId(), replyDto.getParentReplyId());
            return ResponseEntity.ok(new DisplayReplyDto(createdReply));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Find a reply by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reply found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DisplayReplyDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User ID not extractable from token",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Reply not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> findReplyById(
            @Parameter(description = "ID of the reply to retrieve") @PathVariable long id,
            HttpServletRequest request) {
        String userIdStr = extractUserIdFromToken(request);
        if (userIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User ID not extractable from token");
        }
        long userId = parseInt(userIdStr);
        try {
            DisplayReply reply = replyService.findReplyById(id, userId);
            return ResponseEntity.ok(new DisplayReplyDto(reply));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Remove/delete a reply")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reply removed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User ID not extractable from token",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to delete this reply",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Reply not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeReplyFromTweet(
            @Parameter(description = "ID of the reply to remove") @PathVariable long id,
            HttpServletRequest request) {
        String userIdStr = extractUserIdFromToken(request);
        if (userIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User ID not extractable from token");
        }
        long userId = Long.parseLong(userIdStr);
        try {
            replyService.deleteReplyFromTweet(id, userId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @Operation(summary = "Get replies made by a specific user (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user's replies",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User ID not extractable from token",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @GetMapping("/{userId}/user")
    ResponseEntity<?> getRepliesByUserId(
            @Parameter(description = "ID of the user whose replies to retrieve") @PathVariable long userId,
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
            Page<DisplayReply> replies = replyService
                    .getRepliesByUserId(userId, currentUserId, pageable);
            return ResponseEntity.ok(replies.map(DisplayReplyDto::new));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get main replies for a specific tweet (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tweet's main replies",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User ID not extractable from token",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Tweet or User not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @GetMapping("/{tweetId}/tweet")
    public ResponseEntity<?> getRepliesByTweet(
            @Parameter(description = "Page number, 0-indexed") @RequestParam(defaultValue = "0") String page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") String size,
            @Parameter(description = "ID of the tweet whose replies to retrieve") @PathVariable long tweetId,
            HttpServletRequest request
    ) {
        String userIdStr = extractUserIdFromToken(request);
        if (userIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User ID not extractable from token");
        }
        long userId = parseInt(userIdStr);
        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
        try {
            Page<DisplayReply> replies = replyService.getMainRepliesByTweetId(tweetId, userId, pageable);
            return ResponseEntity.ok(replies.map(DisplayReplyDto::new));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get nested replies for a specific parent reply")
    @ApiResponses(value = {
            @ApiResponse(responseCode="200", description="Successfully retrieved nested replies",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = DisplayReplyDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User ID not extractable from token",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Parent reply or User not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @GetMapping("/{replyId}/nested")
    public ResponseEntity<?> getNestedRepliesByReplyId(
            @Parameter(description = "ID of the parent reply") @PathVariable long replyId,
            HttpServletRequest request) {
        String userIdStr = extractUserIdFromToken(request);
        if (userIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User ID not extractable from token");
        }
        long userId = parseInt(userIdStr);
        try {
            List<DisplayReply> replies = replyService.getNestedRepliesByParentReplyId(replyId, userId);
            return ResponseEntity.ok(replies.stream().map(DisplayReplyDto::new).collect(Collectors.toList()));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Like a reply")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reply liked successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DisplayReplyDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User ID in DTO does not match token"),
            @ApiResponse(responseCode = "404", description = "Entity not found (User or Reply)",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "409", description = "Conflict - Reply already liked by this user",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @PostMapping("/like")
    public ResponseEntity<?> createLikeToReply(@RequestBody LikeReplyDto likeDto, HttpServletRequest request) {
        long userId = likeDto.getUserId();
        if (!isValidUser(request, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        LikeReply like = LikeReplyDto.fromDto(likeDto);
        try {
            DisplayReply updatedReply = replyService.createLikeToReply(like, userId, likeDto.getReplyId());
            return ResponseEntity.ok(new DisplayReplyDto(updatedReply));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        } catch (ReactionAlreadyExistsException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.CONFLICT);
        }
    }

    @Operation(summary = "Remove a like from a reply")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Like removed successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DisplayReplyDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User ID not extractable from token",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to remove this like",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Like or Reply not found",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> removeLikeFromReply(
            @Parameter(description = "ID of the like to remove") @PathVariable long id,
            HttpServletRequest request) {
        String userIdStr = extractUserIdFromToken(request);
        if (userIdStr == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User ID not extractable from token");
        }
        long userId = Long.parseLong(userIdStr);
        try {
            DisplayReply reply = replyService.deleteLikeFromReply(id, userId);
            return ResponseEntity.ok(new DisplayReplyDto(reply));
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