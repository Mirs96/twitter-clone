package com.twitterclone.backend.model.services;

import com.twitterclone.backend.dto.DisplayTweetDto;
import com.twitterclone.backend.model.DisplayTweet;
import com.twitterclone.backend.model.entities.Hashtag;
import com.twitterclone.backend.model.entities.LikeTweet;
import com.twitterclone.backend.model.entities.Tweet;
import com.twitterclone.backend.model.entities.User;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.exceptions.ReactionAlreadyExistsException;
import com.twitterclone.backend.model.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class TweetServiceJpa implements TweetService {
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
    public Page<DisplayTweet> getTrendingTweets(Pageable pageable, long userId) {
        Page<DisplayTweet> tweets = tweetRepo
                .getTweetsByLikesAndCommentsDesc(pageable)
                .map(DisplayTweet::new);

        tweets.forEach(t -> {
            t.setLikeCount(likeTweetRepo.countLikesByTweetId(t.getTweet().getId()));
            t.setReplyCount(replyRepo.countReplyByTweetId(t.getTweet().getId()));
            t.setBookmarkCount(bookmarkRepo.countBookmarkByTweetId(t.getTweet().getId()));
            t.setLiked(likeTweetRepo.findLikeByUserIdAndTweetId(userId, t.getTweet().getId()).isPresent());
            t.setBookmarked(bookmarkRepo.findBookmarkByUserIdAndTweetId(userId, t.getTweet().getId()).isPresent());
        });

        return tweets;
    }

    @Override
    public DisplayTweet createLikeToTweet(LikeTweet like, long userId, long tweetId) throws EntityNotFoundException, ReactionAlreadyExistsException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));
        Tweet tweet = tweetRepo.findById(tweetId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", Tweet.class.getName()));

        // If the user has already reacted to the tweet
        if (likeTweetRepo.findLikeByUserIdAndTweetId(userId, tweetId).isPresent()) {
            throw new ReactionAlreadyExistsException("Reaction already present", LikeTweet.class.getName());
        }

        like.setUser(user);
        like.setTweet(tweet);

        likeTweetRepo.save(like);

        // To update the like counts of the tweet
        DisplayTweet tweetDetails = new DisplayTweet(tweet);
        tweetDetails.setLikeCount(likeTweetRepo.countLikesByTweetId(tweetId));
        tweetDetails.setReplyCount(replyRepo.countReplyByTweetId(tweetId));
        tweetDetails.setBookmarkCount(bookmarkRepo.countBookmarkByTweetId(tweetId));
        tweetDetails.setLiked(likeTweetRepo.findLikeByUserIdAndTweetId(userId, tweetId).isPresent());
        tweetDetails.setBookmarked(bookmarkRepo.findBookmarkByUserIdAndTweetId(userId, tweetId).isPresent());

        return tweetDetails;
    }

    public DisplayTweet deleteLikeFromTweet(long tweetId, long userId) throws EntityNotFoundException {
        // if the user, tweet or the like are not in the database throw an exception
        userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));

        DisplayTweet tweet = tweetRepo.findById(tweetId)
                .map(DisplayTweet::new)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", Tweet.class.getName()));

        LikeTweet like = likeTweetRepo.findLikeByUserIdAndTweetId(userId, tweetId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", LikeTweet.class.getName()));

        // delete
        likeTweetRepo.deleteById(like.getId());

        // Return the tweet's details
        tweet.setLikeCount(likeTweetRepo.countLikesByTweetId(tweetId));
        tweet.setReplyCount(replyRepo.countReplyByTweetId(tweetId));
        tweet.setBookmarkCount(bookmarkRepo.countBookmarkByTweetId(tweetId));
        tweet.setLiked(likeTweetRepo.findLikeByUserIdAndTweetId(userId, tweetId).isPresent());
        tweet.setBookmarked(bookmarkRepo.findBookmarkByUserIdAndTweetId(userId, tweetId).isPresent());

        return tweet;
    }
}
