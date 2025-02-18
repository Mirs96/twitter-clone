package com.twitterclone.backend.controllers;

import com.twitterclone.backend.model.EntityNotFoundException;
import com.twitterclone.backend.model.entities.Hashtag;
import com.twitterclone.backend.model.services.HashtagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/hashtag")
public class HashtagController {
    private HashtagService hashtagService;

    @Autowired
    public HashtagController(HashtagService hashtagService) {
        this.hashtagService = hashtagService;
    }

    @PostMapping
    public ResponseEntity<Hashtag> createHashtag(@RequestBody Hashtag hashtag, UriComponentsBuilder uriBuilder) {
        Hashtag savedHashtag = hashtagService.createHashtag(hashtag);
        URI location = uriBuilder.path("/hashtag/{id}").buildAndExpand(hashtag.getId()).toUri();
        return ResponseEntity.created(location).body(savedHashtag);
    }



}
