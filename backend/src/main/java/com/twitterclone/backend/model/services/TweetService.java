package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.entities.Tweet;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TweetService {
    public Tweet createTweet(Tweet tweet, long userId) throws EntityNotFoundException;
    public Page<Tweet> getTrendingTweets(Pageable pageable);
}
