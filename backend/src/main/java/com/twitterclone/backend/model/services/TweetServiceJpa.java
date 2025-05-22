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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TweetServiceJpa implements TweetService {
    private final TweetRepositoryJpa tweetRepo;
    private final UserRepositoryJpa userRepo;
    private final HashtagService hashtagService; // Injected interface
    private final HashtagRepositoryJpa hashtagRepo; // For checking hashtag existence
    private final LikeTweetRepositoryJpa likeTweetRepo;
    private final ReplyRepositoryJpa replyRepo;
    private final BookmarkRepositoryJpa bookmarkRepo;

    public Tweet createTweet(Tweet tweet, long userId) throws EntityNotFoundException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found", User.class.getName()));
        tweet.setUser(user);

        List<Hashtag> linkedHashtags = hashtagService.linkHashtagsToTweet(tweet.getContent());
        tweet.setHashtags(linkedHashtags); // Set the managed/created hashtags
        return tweetRepo.save(tweet);
    }

    @Override
    @Transactional(readOnly = true)
    public DisplayTweet findTweetById(long tweetId, long userId) throws EntityNotFoundException {
        Tweet tweetEntity = tweetRepo.findById(tweetId)
                .orElseThrow(() -> new EntityNotFoundException("Tweet not found", Tweet.class.getName()));
        DisplayTweet tweet = new DisplayTweet(tweetEntity);
        updateTweetDetails(tweet, userId);
        return tweet;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DisplayTweet> getTrendingTweets(Pageable pageable, long userId) throws EntityNotFoundException {
        userRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found", User.class.getName()));

        Page<DisplayTweet> tweets = tweetRepo
                .getTweetsByLikesAndCommentsDesc(pageable)
                .map(DisplayTweet::new);

        tweets.forEach(t -> updateTweetDetails(t, userId));

        return tweets;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DisplayTweet> getTrendingTweetsByFollowedUsers(Pageable pageable, long userId) throws EntityNotFoundException {
        userRepo.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found", User.class.getName()));

        Page<DisplayTweet> tweets = tweetRepo
                .getFollowedUsersTweetsByLikesAndCommentsDesc(userId, pageable)
                .map(DisplayTweet::new);

        tweets.forEach(t -> updateTweetDetails(t, userId));

        return tweets;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DisplayTweet> getTweetByUserId(long userId, long currentUserId, Pageable pageable) throws EntityNotFoundException {
        userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found", User.class.getName()));

        Page<DisplayTweet> tweets = tweetRepo.getTweetsByUserId(userId, pageable)
                .map(DisplayTweet::new);

        tweets.forEach(t -> updateTweetDetails(t, currentUserId));
        return tweets;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DisplayTweet> getTweetsByHashtagId(long hashtagId, Pageable pageable, long currentUserId) throws EntityNotFoundException {
        hashtagRepo.findById(hashtagId)
                .orElseThrow(() -> new EntityNotFoundException("Hashtag not found with ID: " + hashtagId, Hashtag.class.getName()));
        userRepo.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Current user not found", User.class.getName()));

        Page<Tweet> tweetEntitiesPage = tweetRepo.findByHashtags_IdOrderByCreatedAtDescIdDesc(hashtagId, pageable);
        return tweetEntitiesPage.map(tweetEntity -> {
            DisplayTweet displayTweet = new DisplayTweet(tweetEntity);
            updateTweetDetails(displayTweet, currentUserId);
            return displayTweet;
        });
    }

    @Override
    public DisplayTweet createLikeToTweet(LikeTweet like, long userId, long tweetId) throws EntityNotFoundException, ReactionAlreadyExistsException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found", User.class.getName()));
        Tweet tweetEntity = tweetRepo.findById(tweetId)
                .orElseThrow(() -> new EntityNotFoundException("Tweet not found", Tweet.class.getName()));

        if (likeTweetRepo.findLikeByUserIdAndTweetId(userId, tweetId).isPresent()) {
            throw new ReactionAlreadyExistsException("Reaction already present for this tweet", LikeTweet.class.getName());
        }

        like.setUser(user);
        like.setTweet(tweetEntity);
        likeTweetRepo.save(like);

        DisplayTweet tweetDetails = new DisplayTweet(tweetEntity);
        updateTweetDetails(tweetDetails, userId);
        return tweetDetails;
    }

    @Override
    public DisplayTweet deleteLikeFromTweet(long id, long userId) throws EntityNotFoundException, UnauthorizedException {
        LikeTweet like = likeTweetRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Like not found for this tweet", LikeTweet.class.getName()));

        if (like.getUser().getId() != userId) {
            throw new UnauthorizedException("User is not authorized to delete this like", LikeTweet.class.getName());
        }
        
        Tweet likedTweet = like.getTweet();
        likeTweetRepo.deleteById(like.getId());

        DisplayTweet tweetDetails = new DisplayTweet(likedTweet);
        updateTweetDetails(tweetDetails, userId);
        return tweetDetails;
    }

    @Override
    public DisplayTweet createBookmarkToTweet(Bookmark bookmark, long userId, long tweetId) throws EntityNotFoundException, ReactionAlreadyExistsException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found", User.class.getName()));
        Tweet tweetEntity = tweetRepo.findById(tweetId)
                .orElseThrow(() -> new EntityNotFoundException("Tweet not found", Tweet.class.getName()));

        if (bookmarkRepo.findBookmarkByUserIdAndTweetId(userId, tweetId).isPresent()) {
            throw new ReactionAlreadyExistsException("Bookmark already present for this tweet", Bookmark.class.getName());
        }

        bookmark.setUser(user);
        bookmark.setTweet(tweetEntity);
        bookmarkRepo.save(bookmark);

        DisplayTweet tweetDetails = new DisplayTweet(tweetEntity);
        updateTweetDetails(tweetDetails, userId);
        return tweetDetails;
    }

    @Override
    public DisplayTweet deleteBookmarkFromTweet(long id, long userId) throws EntityNotFoundException, UnauthorizedException {
        Bookmark bookmark = bookmarkRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bookmark not found", Bookmark.class.getName()));

        if (bookmark.getUser().getId() != userId) {
            throw new UnauthorizedException("User is not authorized to delete this bookmark", Bookmark.class.getName());
        }

        Tweet bookmarkedTweet = bookmark.getTweet();
        bookmarkRepo.deleteById(bookmark.getId());

        DisplayTweet tweetDetails = new DisplayTweet(bookmarkedTweet);
        updateTweetDetails(tweetDetails, userId); // userId is the current user checking details
        return tweetDetails;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DisplayTweet> getBookmarkedTweetsByUserId(long userId, Pageable pageable) throws EntityNotFoundException {
        userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found", User.class.getName()));

        Page<Bookmark> bookmarksPage = bookmarkRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        return bookmarksPage.map(bookmark -> {
            DisplayTweet displayTweet = new DisplayTweet(bookmark.getTweet());
            updateTweetDetails(displayTweet, userId); // userId here is the logged-in user viewing their own bookmarks
            return displayTweet;
        });
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
