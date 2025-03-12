package com.twitterclone.backend.model.repositories;

import com.twitterclone.backend.model.entities.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReplyRepositoryJpa extends JpaRepository<Reply, Long> {
    @Query("""
                SELECT COUNT(r)
                FROM Reply r
                WHERE r.tweet.id = :tweetId
            """)
    public long countReplyByTweetId(@Param("tweetId") long tweetId);
}
