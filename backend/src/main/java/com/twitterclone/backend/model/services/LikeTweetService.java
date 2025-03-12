package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.entities.LikeTweet;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;

public interface LikeTweetService {
    public LikeTweet createLikeToTweet(LikeTweet like, long userId, long tweetId) throws EntityNotFoundException;
}
