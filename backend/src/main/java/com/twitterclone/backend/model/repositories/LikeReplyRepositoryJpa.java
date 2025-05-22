package com.twitterclone.backend.model.repositories;

import com.twitterclone.backend.model.entities.LikeReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikeReplyRepositoryJpa extends JpaRepository<LikeReply, Long> {
    @Query("""
                SELECT COUNT(lr)
                FROM LikeReply lr
                WHERE lr.reply.id = :replyId
            """)
    long countLikesByReplyId(@Param("replyId") long parentReplyId);

    @Query("""
                SELECT lr
                FROM LikeReply lr
                WHERE lr.reply.id = :replyId
                AND lr.user.id = :userId
            """)
    Optional<LikeReply> findLikeByUserIdAndReplyId(@Param("userId") long userId, @Param("replyId") long parentReplyId);
}
