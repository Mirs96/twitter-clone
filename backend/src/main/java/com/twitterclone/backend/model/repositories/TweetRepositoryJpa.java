package com.twitterclone.backend.model.repositories;

import com.twitterclone.backend.model.entities.Tweet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TweetRepositoryJpa extends JpaRepository<Tweet, Long> {
    @Query("""
                SELECT t
                FROM Tweet t
                ORDER BY t.creationDate DESC, (
                    SELECT COUNT(lt)
                    FROM LikeTweet lt
                    WHERE lt.tweet = t
                ) DESC, (
                    SELECT COUNT(r)
                    FROM Reply r
                    WHERE r.tweet = t
                ) DESC
            """)
    public Page<Tweet> getTweetsByLikesAndCommentsDesc(Pageable pageable);
}
