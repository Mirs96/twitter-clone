package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.DisplayTweet;
import com.twitterclone.backend.model.entities.*;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.exceptions.ReactionAlreadyExistsException;
import com.twitterclone.backend.model.exceptions.UnauthorizedException;
import com.twitterclone.backend.model.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TweetServiceJpa implements TweetService {
    private final TweetRepositoryJpa tweetRepo;
    private final UserRepositoryJpa userRepo;
    private final HashtagService hashtagService;
    private final LikeTweetRepositoryJpa likeTweetRepo;
    private final ReplyRepositoryJpa replyRepo;
    private final BookmarkRepositoryJpa bookmarkRepo;

    public Tweet createTweet(Tweet tweet, long userId) throws EntityNotFoundException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));
        tweet.setUser(user);

        List<Hashtag> hashtags = hashtagService.linkHashtagsToTweet(tweet.getContent());
        tweet.setHashtags(hashtags);
        return tweetRepo.save(tweet);
    }

    @Override
    public DisplayTweet findTweetById(long tweetId, long userId) throws EntityNotFoundException {
        DisplayTweet tweet= tweetRepo.findById(tweetId)
                .map(DisplayTweet::new)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", Tweet.class.getName()));

        updateTweetDetails(tweet, userId);

        return tweet;
    }

    @Override
    public Page<DisplayTweet> getTrendingTweets(Pageable pageable, long userId) throws EntityNotFoundException {
        userRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));

        Page<DisplayTweet> tweets = tweetRepo
                .getTweetsByLikesAndCommentsDesc(pageable)
                .map(DisplayTweet::new);

        tweets.forEach(t -> updateTweetDetails(t, userId));

        return tweets;
    }

    @Override
    public Page<DisplayTweet> getTrendingTweetsByFollowedUsers(Pageable pageable, long userId) throws EntityNotFoundException {
        userRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));

        Page<DisplayTweet> tweets = tweetRepo
                .getFollowedUsersTweetsByLikesAndCommentsDesc(userId, pageable)
                .map(DisplayTweet::new);

        tweets.forEach(t -> updateTweetDetails(t, userId));

        return tweets;
    }

    @Override
    public Page<DisplayTweet> getTweetByUserId(long userId, long currentUserId, Pageable pageable) throws EntityNotFoundException {
        userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));

        Page<DisplayTweet> tweets = tweetRepo.getTweetsByUserId(userId, pageable)
                .map(DisplayTweet::new);

        tweets.forEach(t -> updateTweetDetails(t, currentUserId));
        return tweets;
    }

    @Override
    public DisplayTweet createLikeToTweet(LikeTweet like, long userId, long tweetId) throws EntityNotFoundException, ReactionAlreadyExistsException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));
        Tweet tweet = tweetRepo.findById(tweetId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", Tweet.class.getName()));

        // Check if the user already liked the tweet
        if (likeTweetRepo.findLikeByUserIdAndTweetId(userId, tweetId).isPresent()) {
            throw new ReactionAlreadyExistsException("Reaction already present", LikeTweet.class.getName());
        }

        like.setUser(user);
        like.setTweet(tweet);

        likeTweetRepo.save(like);

        // Update tweet details after like
        DisplayTweet tweetDetails = new DisplayTweet(tweet);
        updateTweetDetails(tweetDetails, userId);

        return tweetDetails;
    }

    @Override
    public DisplayTweet deleteLikeFromTweet(long id, long userId) throws EntityNotFoundException, UnauthorizedException {
        LikeTweet like = likeTweetRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", LikeTweet.class.getName()));

        if (like.getUser().getId() != userId) {
            throw new UnauthorizedException("User is not authorized to delete this", LikeTweet.class.getName());
        }

        DisplayTweet tweet = new DisplayTweet(like.getTweet());

        likeTweetRepo.deleteById(like.getId());

        // Update tweet details after like deletion
        updateTweetDetails(tweet, userId);

        return tweet;
    }

    @Override
    public DisplayTweet createBookmarkToTweet(Bookmark bookmark, long userId, long tweetId) throws EntityNotFoundException, ReactionAlreadyExistsException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));
        Tweet tweet = tweetRepo.findById(tweetId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", Tweet.class.getName()));

        // Check if the user already bookmarked the tweet
        if (bookmarkRepo.findBookmarkByUserIdAndTweetId(userId, tweetId).isPresent()) {
            throw new ReactionAlreadyExistsException("Reaction already present", Bookmark.class.getName());
        }

        bookmark.setUser(user);
        bookmark.setTweet(tweet);

        bookmarkRepo.save(bookmark);

        // Update tweet details after bookmark
        DisplayTweet tweetDetails = new DisplayTweet(tweet);
        updateTweetDetails(tweetDetails, userId);

        return tweetDetails;
    }

    @Override
    public DisplayTweet deleteBookmarkFromTweet(long id, long userId) throws EntityNotFoundException, UnauthorizedException {
        Bookmark bookmark = bookmarkRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", Bookmark.class.getName()));

        if (bookmark.getUser().getId() != userId) {
            throw new UnauthorizedException("User is not authorized to delete this", Bookmark.class.getName());
        }

        DisplayTweet tweet = new DisplayTweet(bookmark.getTweet());

        bookmarkRepo.deleteById(bookmark.getId());

        // Update tweet details after bookmark deletion
        updateTweetDetails(tweet, userId);

        return tweet;
    }

    private void updateTweetDetails(DisplayTweet tweetDetails, long userId) {
        long tweetId = tweetDetails.getTweet().getId();

        tweetDetails.setLikeCount(likeTweetRepo.countLikesByTweetId(tweetId));
        tweetDetails.setReplyCount(replyRepo.countReplyByTweetId(tweetId));
        tweetDetails.setBookmarkCount(bookmarkRepo.countBookmarkByTweetId(tweetId));

        Optional<LikeTweet> oLike = likeTweetRepo.findLikeByUserIdAndTweetId(userId, tweetId);
        tweetDetails.setLiked(oLike.isPresent());
        oLike.ifPresent(l -> tweetDetails.setLikeId(l.getId()));

        Optional<Bookmark> oBookmark = bookmarkRepo.findBookmarkByUserIdAndTweetId(userId, tweetId);
        tweetDetails.setBookmarked(oBookmark.isPresent());
        oBookmark.ifPresent(b -> tweetDetails.setBookmarkId(b.getId()));
    }
}
