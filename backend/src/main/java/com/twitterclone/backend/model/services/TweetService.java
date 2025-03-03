package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.entities.Tweet;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;

public interface TweetService {
    public Tweet createTweet(Tweet tweet, long userId) throws EntityNotFoundException;
}
