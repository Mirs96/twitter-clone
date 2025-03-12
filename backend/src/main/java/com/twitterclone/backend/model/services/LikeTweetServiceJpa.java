package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.entities.LikeTweet;
import com.twitterclone.backend.model.entities.Tweet;
import com.twitterclone.backend.model.entities.User;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.repositories.LikeTweetRepositoryJpa;
import com.twitterclone.backend.model.repositories.TweetRepositoryJpa;
import com.twitterclone.backend.model.repositories.UserRepositoryJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeTweetServiceJpa implements LikeTweetService {
    private TweetRepositoryJpa tweetRepo;
    private UserRepositoryJpa userRepo;
    private LikeTweetRepositoryJpa likeTweetRepo;

    @Autowired
    public LikeTweetServiceJpa(TweetRepositoryJpa tweetRepo, UserRepositoryJpa userRepo, LikeTweetRepositoryJpa likeTweetRepo) {
        this.tweetRepo = tweetRepo;
        this.userRepo = userRepo;
        this.likeTweetRepo = likeTweetRepo;
    }

    public LikeTweet createLikeToTweet(LikeTweet like, long userId, long tweetId) throws EntityNotFoundException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));
        Tweet tweet = tweetRepo.findById(tweetId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", Tweet.class.getName()));

        like.setUser(user);
        like.setTweet(tweet);

        return likeTweetRepo.save(like);
    }
}
