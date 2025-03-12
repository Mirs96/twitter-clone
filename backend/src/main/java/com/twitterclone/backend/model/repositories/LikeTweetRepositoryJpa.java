package com.twitterclone.backend.model.repositories;

import com.twitterclone.backend.model.entities.LikeTweet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeTweetRepositoryJpa extends JpaRepository<LikeTweet, Long> {
}
