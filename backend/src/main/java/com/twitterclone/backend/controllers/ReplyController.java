package com.twitterclone.backend.controllers;

import com.twitterclone.backend.dto.DisplayTweetDto;
import com.twitterclone.backend.dto.ReplyDto;
import com.twitterclone.backend.model.DisplayTweet;
import com.twitterclone.backend.model.entities.Reply;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
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
    public ResponseEntity<?> createReplyToTweet(@RequestBody ReplyDto replyDto, HttpServletRequest request) {
        long userId = replyDto.getUserId();

        if (!isValidUser(request, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Reply reply = ReplyDto.fromDto(replyDto);
        try {
            Reply createdReply = replyService.createReplyToTweet(reply, userId, replyDto.getTweetId(), replyDto.getParentReplyId());
            return ResponseEntity.ok(new ReplyDto(createdReply));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getFullMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeReplyToTweet(@PathVariable long id, HttpServletRequest request) {
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

    @GetMapping("/{tweetId}")
    public ResponseEntity<Page<ReplyDto>> getReplyByTweet(
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "20") String size,
            @PathVariable long tweetId) {
        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
        Page<Reply> replies = replyService.getReplyByTweetId(tweetId, pageable);
        return ResponseEntity.ok(replies.map(ReplyDto::new));
    }
}
