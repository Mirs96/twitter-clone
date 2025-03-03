package com.twitterclone.backend.model.repositories;

import com.twitterclone.backend.model.entities.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TweetRepositoryJpa extends JpaRepository<Tweet, Long> {
}
