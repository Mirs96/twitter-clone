package com.twitterclone.backend.controllers;

import com.twitterclone.backend.model.entities.Hashtag;
import com.twitterclone.backend.model.services.HashtagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@CrossOrigin(origins={"http://localhost:4200", "http://localhost:5173"}, allowedHeaders = "*")
@RestController
@RequestMapping("/api/hashtag")
public class HashtagController {
    private HashtagService hashtagService;

    @Autowired
    public HashtagController(HashtagService hashtagService) {
        this.hashtagService = hashtagService;
    }

    @PostMapping
    public ResponseEntity<Hashtag> createHashtag(@RequestBody Hashtag hashtag, UriComponentsBuilder uriBuilder) {
        Hashtag savedHashtag = hashtagService.createHashtag(hashtag);
        URI location = uriBuilder.path("/hashtag/{id}").buildAndExpand(savedHashtag.getId()).toUri();
        return ResponseEntity.created(location).body(savedHashtag);
    }
}
