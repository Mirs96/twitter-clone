package com.twitterclone.backend.model.repositories;

import com.twitterclone.backend.model.entities.Bookmark;
import com.twitterclone.backend.model.entities.LikeTweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookmarkRepositoryJpa extends JpaRepository<Bookmark, Long> {
    @Query("""
                SELECT COUNT(b)
                FROM Bookmark b
                WHERE b.tweet.id = :tweetId
            """)
    public long countBookmarkByTweetId(@Param("tweetId") long tweetId);

    @Query("""
                SELECT b
                FROM Bookmark b
                WHERE b.tweet.id = :tweetId
                AND b.user.id = :userId
            """)
    public Optional<Bookmark> findBookmarkByUserIdAndTweetId(@Param("userId") long userId, @Param("tweetId") long tweetId);
}
