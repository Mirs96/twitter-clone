package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.entities.Hashtag;
import com.twitterclone.backend.model.repositories.HashtagRepositoryJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HashtagServiceJpa implements HashtagService{
    private HashtagRepositoryJpa hashtagRepo;

    @Autowired
    public HashtagServiceJpa(HashtagRepositoryJpa hashtagRepo) {
        this.hashtagRepo = hashtagRepo;
    }

    public Hashtag createHashtag(Hashtag hashtag) {
        return hashtagRepo.save(hashtag);
    }
}
