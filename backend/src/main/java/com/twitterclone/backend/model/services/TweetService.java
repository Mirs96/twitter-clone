package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.DisplayTweet;
import com.twitterclone.backend.model.entities.Bookmark;
import com.twitterclone.backend.model.entities.LikeTweet;
import com.twitterclone.backend.model.entities.Reply;
import com.twitterclone.backend.model.entities.Tweet;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.exceptions.ReactionAlreadyExistsException;
import com.twitterclone.backend.model.exceptions.UnauthorizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TweetService {
    Tweet createTweet(Tweet tweet, long userId) throws EntityNotFoundException;
    DisplayTweet findTweetById(long tweetId, long userId) throws EntityNotFoundException;
    Page<DisplayTweet> getTrendingTweets(Pageable pageable, long userId);

    DisplayTweet createLikeToTweet(LikeTweet like, long userId, long tweetId) throws EntityNotFoundException, ReactionAlreadyExistsException;
    DisplayTweet deleteLikeFromTweet(long id, long userId) throws EntityNotFoundException, UnauthorizedException;

    DisplayTweet createBookmarkToTweet(Bookmark bookmark, long userId, long tweetId) throws EntityNotFoundException, ReactionAlreadyExistsException;
    DisplayTweet deleteBookmarkFromTweet(long id, long userId) throws EntityNotFoundException, UnauthorizedException;
}
