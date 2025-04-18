package com.twitterclone.backend.model.repositories;

import com.twitterclone.backend.model.entities.Follower;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowerRepositoryJpa extends JpaRepository<Follower, Long> {
    long countByUserId(long userId); // followers
    long countByFollowerId(long followerId); // following

    Page<Follower> findByUserId(long userId, Pageable pageable); // followers
    Page<Follower> findByFollowerId(long followerId, Pageable pageable); // following
}
