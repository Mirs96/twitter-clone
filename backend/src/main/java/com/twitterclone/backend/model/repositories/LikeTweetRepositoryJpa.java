package com.twitterclone.backend.model.repositories;

import com.twitterclone.backend.model.entities.LikeTweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeTweetRepositoryJpa extends JpaRepository<LikeTweet, Long> {
    @Query("""
                SELECT COUNT(lt)
                FROM LikeTweet lt
                WHERE lt.tweet.id = :tweetId
            """)
    public long countLikesByTweetId(@Param("tweetId") long tweetId);
}
