package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.entities.Hashtag;
import com.twitterclone.backend.model.entities.Tweet;
import com.twitterclone.backend.model.entities.User;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.repositories.HashtagRepositoryJpa;
import com.twitterclone.backend.model.repositories.TweetRepositoryJpa;
import com.twitterclone.backend.model.repositories.UserRepositoryJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TweetServiceJpa implements  TweetService {
    private TweetRepositoryJpa tweetRepo;
    private UserRepositoryJpa userRepo;
    private HashtagService hashtagService;

    @Autowired
    public TweetServiceJpa(TweetRepositoryJpa tweetRepo, UserRepositoryJpa userRepo, HashtagService hashtagService) {
        this.tweetRepo = tweetRepo;
        this.userRepo = userRepo;
        this.hashtagService = hashtagService;
    }

    public Tweet createTweet(Tweet tweet, long userId) throws EntityNotFoundException {
        Optional<User> optUser = userRepo.findById(userId);
        if (optUser.isEmpty()) {
            throw new EntityNotFoundException("Entity not found", User.class.getName());
        }
        tweet.setUser(optUser.get());

        List<Hashtag> hashtags = hashtagService.createHashtagsFromTweet(tweet.getContent());
        tweet.setHashtags(hashtags);
        return tweetRepo.save(tweet);
    }
}
