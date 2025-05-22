package com.twitterclone.backend.model.repositories;

import com.twitterclone.backend.model.entities.Follower;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FollowerRepositoryJpa extends JpaRepository<Follower, Long> {
    long countByUserId(long userId);
    long countByFollowerId(long followerId);

    Page<Follower> findByUserId(long userId, Pageable pageable);
    Page<Follower> findByFollowerId(long followerId, Pageable pageable);

    @Query("""
                SELECT f
                FROM Follower f
                WHERE f.follower.id = :followerId
                AND f.user.id = :userId
    """)
    Optional<Follower> findByFollowerAndUser(@Param("followerId") long followerId, @Param("userId") long userId);
}
