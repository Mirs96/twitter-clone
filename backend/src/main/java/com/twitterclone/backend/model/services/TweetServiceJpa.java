package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.DisplayTweet;
import com.twitterclone.backend.model.entities.Hashtag;
import com.twitterclone.backend.model.entities.Tweet;
import com.twitterclone.backend.model.entities.User;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TweetServiceJpa implements  TweetService {
    private TweetRepositoryJpa tweetRepo;
    private UserRepositoryJpa userRepo;
    private HashtagService hashtagService;
    private LikeTweetRepositoryJpa likeTweetRepo;
    private ReplyRepositoryJpa replyRepo;
    private BookmarkRepositoryJpa bookmarkRepo;

    @Autowired
    public TweetServiceJpa(TweetRepositoryJpa tweetRepo, UserRepositoryJpa userRepo, HashtagService hashtagService, LikeTweetRepositoryJpa likeTweetRepo, ReplyRepositoryJpa replyRepo, BookmarkRepositoryJpa bookmarkRepo) {
        this.tweetRepo = tweetRepo;
        this.userRepo = userRepo;
        this.hashtagService = hashtagService;
        this.likeTweetRepo = likeTweetRepo;
        this.replyRepo = replyRepo;
        this.bookmarkRepo = bookmarkRepo;
    }

    public Tweet createTweet(Tweet tweet, long userId) throws EntityNotFoundException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));
        tweet.setUser(user);

        List<Hashtag> hashtags = hashtagService.createHashtagsFromTweet(tweet.getContent());
        tweet.setHashtags(hashtags);
        return tweetRepo.save(tweet);
    }

    @Override
    public Page<DisplayTweet> getTrendingTweets(Pageable pageable) {
        Page<DisplayTweet> tweets = tweetRepo
                .getTweetsByLikesAndCommentsDesc(pageable)
                .map(DisplayTweet::new);

        tweets.forEach(t -> {
            t.setLikeCount(likeTweetRepo.countLikesByTweetId(t.getTweet().getId()));
            t.setReplyCount(replyRepo.countReplyByTweetId(t.getTweet().getId()));
            t.setBookmarkCount(bookmarkRepo.countBookmarkByTweetId(t.getTweet().getId()));
        });

        return tweets;
    }
}
