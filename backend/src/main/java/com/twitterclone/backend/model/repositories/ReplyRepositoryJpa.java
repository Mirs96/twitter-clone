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
                AND r.parentReply IS NULL
                ORDER BY
                    (EXTRACT(EPOCH FROM r.createdAt) * 0.7) +
                    (
                        (
                            SELECT COUNT(lr)
                            FROM LikeReply lr
                            WHERE lr.reply = r
                        ) +
                        (
                        SELECT COUNT(r2)
                        FROM Reply r2
                        WHERE r2.parentReply = r
                        )
                    ) * 0.3 DESC
            """)
    Page<Reply> findMainRepliesByTweet(@Param("tweetId") long tweetId, Pageable pageable);

    @Query("""
                SELECT r
                FROM Reply r
                WHERE r.parentReply.id = :parentReplyId
                ORDER BY
                    (EXTRACT(EPOCH FROM r.createdAt) * 0.7) +
                    (
                        (
                            SELECT COUNT(lr)
                            FROM LikeReply lr
                            WHERE lr.reply = r
                        ) +
                        (
                        SELECT COUNT(r2)
                        FROM Reply r2
                        WHERE r2.parentReply = r
                        )
                    ) * 0.3 DESC
            """)
    List<Reply> findNestedRepliesByParentReplyId(@Param("parentReplyId") long parentReplyId);

    @Query("""
                SELECT COUNT(r)
                FROM Reply r
                WHERE r.tweet.id = :tweetId
            """)
    long countReplyByTweetId(@Param("tweetId") long tweetId);

    @Query("""
                SELECT COUNT(r)
                FROM Reply r
                WHERE r.parentReply.id = :replyId
            """)
    long countReplyByParentReplyId(@Param("replyId") long replyId);

    @Query("""
                SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
                FROM Reply r
                WHERE r.parentReply.id = :replyId
            """)
    boolean hasNestedReplies(@Param("replyId") long replyId);

    @Query("""
                SELECT r
                FROM Reply r
                WHERE r.user.id =:userId
                ORDER BY r.createdAt DESC
            """)
    Page<Reply> getRepliesByUserId(@Param("userId") long userId, Pageable pageable);
}
