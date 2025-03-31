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
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.Integer.parseInt;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
@RestController
@RequestMapping("/api/reply")
public class ReplyController {

    private ReplyService replyService;
    private JwtService jwtService;

    @Autowired
    public ReplyController(ReplyService replyService, JwtService jwtService) {
        this.replyService = replyService;
        this.jwtService = jwtService;
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<?> findReplyById(@PathVariable long id, HttpServletRequest request) {
        long userId = parseInt(extractUserIdFromToken(request));
        try {
            DisplayReply reply = replyService.findReplyById(id, userId);
            return ResponseEntity.ok(new DisplayReplyDto(reply));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeReplyFromTweet(@PathVariable long id, HttpServletRequest request) {
        long userId = Long.parseLong(extractUserIdFromToken(request));

        try {
            replyService.deleteReplyFromTweet(id, userId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{tweetId}/tweet")
    public ResponseEntity<?> getRepliesByTweet(
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "10") String size,
            @PathVariable long tweetId,
            HttpServletRequest request
    ) {
        long userId = parseInt(extractUserIdFromToken(request));

        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
        try {
            Page<DisplayReply> replies = replyService.getMainRepliesByTweetId(tweetId, userId, pageable);
            return ResponseEntity.ok(replies.map(DisplayReplyDto::new));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{replyId}/nested")
    public ResponseEntity<?> getNestedRepliesByReplyId(@PathVariable long replyId, HttpServletRequest request) {
        long userId = parseInt(extractUserIdFromToken(request));

        try {
            List<DisplayReply> replies = replyService.getNestedRepliesByParentReplyId(replyId, userId);
            return ResponseEntity.ok(replies.stream().map(DisplayReplyDto::new).toList());
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

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

    @DeleteMapping("/{id}/like")
    public ResponseEntity<?> removeLikeToTweet(@PathVariable long id, HttpServletRequest request) {
        long userId = Long.parseLong(extractUserIdFromToken(request));

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
