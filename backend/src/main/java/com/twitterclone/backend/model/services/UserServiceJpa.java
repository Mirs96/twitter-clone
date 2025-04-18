package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.DisplayReply;
import com.twitterclone.backend.model.DisplayTweet;
import com.twitterclone.backend.model.entities.*;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceJpa implements UserService {
    private UserRepositoryJpa userRepo;
    private TweetRepositoryJpa tweetRepo;
    private ReplyRepositoryJpa replyRepo;
    private LikeTweetRepositoryJpa likeTweetRepo;
    private BookmarkRepositoryJpa bookmarkRepo;
    private FollowerRepositoryJpa followerRepo;
    private LikeReplyRepositoryJpa likeReplyRepo;

    @Autowired
    public UserServiceJpa(UserRepositoryJpa userRepo, TweetRepositoryJpa tweetRepo, ReplyRepositoryJpa replyRepo, LikeTweetRepositoryJpa likeTweetRepo, BookmarkRepositoryJpa bookmarkRepo, FollowerRepositoryJpa followerRepo, LikeReplyRepositoryJpa likeReplyRepo) {
        this.userRepo = userRepo;
        this.tweetRepo = tweetRepo;
        this.replyRepo = replyRepo;
        this.likeTweetRepo = likeTweetRepo;
        this.bookmarkRepo = bookmarkRepo;
        this.followerRepo = followerRepo;
        this.likeReplyRepo = likeReplyRepo;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userRepo.findById(userId);
    }

    @Override
    public long countFollowersByUserId(long userId) throws EntityNotFoundException {
        userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));

        return followerRepo.countByUserId(userId);
    }

    @Override
    public long countFollowingByFollowerId(long followerId) throws EntityNotFoundException {
        userRepo.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));

        return followerRepo.countByFollowerId(followerId);
    }

    @Override
    public Page<Follower> findFollowersByUserId(long userId, Pageable pageable) throws EntityNotFoundException {
        userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));

        return followerRepo.findByUserId(userId, pageable);
    }

    @Override
    public Page<Follower> findFollowingByFollowerId(long followerId, Pageable pageable) throws EntityNotFoundException {
        userRepo.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));

        return followerRepo.findByFollowerId(followerId, pageable);
    }
}
