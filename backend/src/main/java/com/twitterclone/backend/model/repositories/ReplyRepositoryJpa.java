package com.twitterclone.backend.model.repositories;

import com.twitterclone.backend.model.entities.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReplyRepositoryJpa extends JpaRepository<Reply, Long> {

    @Query("""
                SELECT r
                FROM Reply r
                WHERE r.tweet.id = :tweetId
            """)
    Page<Reply> getReplyByTweetId(@Param("tweetId") long tweetId, Pageable pageable);

    @Query("""
                SELECT COUNT(r)
                FROM Reply r
                WHERE r.tweet.id = :tweetId
            """)
    long countReplyByTweetId(@Param("tweetId") long tweetId);

    @Query("""
                SELECT r
                FROM Reply r
                WHERE r.tweet.id = :tweetId
                AND r.user.id = :userId
            """)
    List<Reply> findReplyByUserIdAndTweetId(@Param("userId") long userId, @Param("tweetId") long tweetId);

}
