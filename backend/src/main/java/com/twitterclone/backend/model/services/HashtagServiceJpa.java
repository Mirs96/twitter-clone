package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.entities.Hashtag;
import com.twitterclone.backend.model.repositories.HashtagRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HashtagServiceJpa implements HashtagService {
    private final HashtagRepositoryJpa hashtagRepo;

    public Hashtag createHashtag(Hashtag hashtag) {
        return hashtagRepo.save(hashtag);
    }

    public List<Hashtag> linkHashtagsToTweet(String content) {
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

        return tags.stream()
                .filter(t -> hashtagRepo.findByTag(t).isEmpty())
                .map(t -> Hashtag.builder().tag(t).build())
                .map(hashtagRepo::save)
                .toList();
    }
}
