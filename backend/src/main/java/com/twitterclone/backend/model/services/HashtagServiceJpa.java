package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.entities.Hashtag;
import com.twitterclone.backend.model.repositories.HashtagRepositoryJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HashtagServiceJpa implements HashtagService {
    private HashtagRepositoryJpa hashtagRepo;

    @Autowired
    public HashtagServiceJpa(HashtagRepositoryJpa hashtagRepo) {
        this.hashtagRepo = hashtagRepo;
    }

    public Hashtag createHashtag(Hashtag hashtag) {
        return hashtagRepo.save(hashtag);
    }

    public List<Hashtag> createHashtagsFromTweet(String content) {
        if (!content.contains("#")) {
            return new ArrayList<>();
        }

        // split by one or more (+) spaces (\\s)
        String[] words = content.split("\\s+");

        Set<String> tags = new HashSet<>();
        for (String word : words) {
            if (word.startsWith("#")) {
                // [] is a sequence of characters, ^ is a negation (every character that is not...
                // #, a letter, a number or an underscore, + at the end means it removes more than
                // one character. The ^ is the negation that tells to replaceAll what character to not touch.
                tags.add(word.replaceAll("[^#\\w]+", ""));
            }
        }
        List<Hashtag> hashtags = tags.stream()
                .filter(t -> hashtagRepo.findByTag(t).isEmpty())
                .map(t -> new Hashtag(0, t))
                .map(hashtagRepo::save)
                .toList();

        return hashtags;
    }
}
