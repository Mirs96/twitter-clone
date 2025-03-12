package com.twitterclone.backend.model.repositories;

import com.twitterclone.backend.model.entities.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookmarkRepositoryJpa extends JpaRepository<Bookmark, Long> {
    @Query("""
                SELECT COUNT(b)
                FROM Bookmark b
                WHERE b.tweet.id = :tweetId
            """)
    public long countBookmarkByTweetId(@Param("tweetId") long tweetId);
}
