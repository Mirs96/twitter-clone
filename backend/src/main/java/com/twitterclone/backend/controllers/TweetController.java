package com.twitterclone.backend.controllers;

import com.twitterclone.backend.dto.TweetDto;
import com.twitterclone.backend.model.entities.Hashtag;
import com.twitterclone.backend.model.entities.Tweet;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.services.HashtagService;
import com.twitterclone.backend.model.services.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tweet")
public class TweetController {
    private TweetService tweetService;
    private HashtagService hashtagService;

    @Autowired
    public TweetController(TweetService tweetService, HashtagService hashtagService) {
        this.tweetService = tweetService;
        this.hashtagService = hashtagService;
    }

    @PostMapping
    public ResponseEntity<?> createTweet(
            @RequestBody TweetDto tweetDto,
            UriComponentsBuilder uriBuilder) {
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
