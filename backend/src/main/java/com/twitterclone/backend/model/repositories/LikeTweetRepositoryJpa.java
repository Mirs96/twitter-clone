package com.twitterclone.backend.model.repositories;

import com.twitterclone.backend.model.entities.LikeTweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeTweetRepositoryJpa extends JpaRepository<LikeTweet, Long> {
    @Query("""
                SELECT COUNT(lt)
                FROM LikeTweet lt
                WHERE lt.tweet.id = :tweetId
            """)
    long countLikesByTweetId(@Param("tweetId") long tweetId);

    @Query("""
                SELECT lt
                FROM LikeTweet lt
                WHERE lt.tweet.id = :tweetId
                AND lt.user.id = :userId
            """)
    Optional<LikeTweet> findLikeByUserIdAndTweetId(@Param("userId") long userId, @Param("tweetId") long tweetId);
}
