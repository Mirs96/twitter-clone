package com.twitterclone.backend.model.repositories;

import com.twitterclone.backend.model.entities.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    long countBookmarkByTweetId(@Param("tweetId") long tweetId);

    @Query("""
                SELECT b
                FROM Bookmark b
                WHERE b.tweet.id = :tweetId
                AND b.user.id = :userId
            """)
    Optional<Bookmark> findBookmarkByUserIdAndTweetId(@Param("userId") long userId, @Param("tweetId") long tweetId);

    Page<Bookmark> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
