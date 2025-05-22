package com.twitterclone.backend.model.repositories;

import com.twitterclone.backend.model.entities.Tweet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TweetRepositoryJpa extends JpaRepository<Tweet, Long> {
    @Query(value = """
                SELECT t
                FROM Tweet t
                ORDER BY
                    EXTRACT(EPOCH FROM CAST(t.createdAt AS TIMESTAMP)) * 0.7 +
                    (
                        (
                            SELECT COUNT(lt.id)
                            FROM LikeTweet lt
                            WHERE lt.tweet = t
                        ) +
                        (
                            SELECT COUNT(r.id)
                            FROM Reply r
                            WHERE r.tweet = t
                        )
                    ) * 0.3
                DESC
            """,
            countQuery = "SELECT count(t) FROM Tweet t")
    Page<Tweet> getTweetsByLikesAndCommentsDesc(Pageable pageable);

    @Query(value = """
                SELECT t
                FROM Tweet t
                WHERE EXISTS (
                    SELECT 1
                    FROM Follower f
                    WHERE f.follower.id = :loggedInUserId AND f.user = t.user
                )
                ORDER BY
                    EXTRACT(EPOCH FROM CAST(t.createdAt AS TIMESTAMP)) * 0.7 +
                    (
                        (
                            SELECT COUNT(lt.id)
                            FROM LikeTweet lt
                            WHERE lt.tweet = t
                        ) +
                        (
                            SELECT COUNT(r.id)
                            FROM Reply r
                            WHERE r.tweet = t
                        )
                    ) * 0.3
                DESC
            """,
            countQuery = """
                SELECT count(t)
                FROM Tweet t
                WHERE EXISTS (
                    SELECT 1
                    FROM Follower f
                    WHERE f.follower.id = :loggedInUserId AND f.user = t.user)
            """)
    Page<Tweet> getFollowedUsersTweetsByLikesAndCommentsDesc(@Param("loggedInUserId") Long loggedInUserId, Pageable pageable);

    @Query("""
                SELECT t
                FROM Tweet t
                WHERE t.user.id =:userId
                ORDER BY t.createdAt DESC
            """)
    Page<Tweet> getTweetsByUserId(@Param("userId") long userId, Pageable pageable);

    Page<Tweet> findByHashtags_IdOrderByCreatedAtDescIdDesc(long hashtagId, Pageable pageable);
}
