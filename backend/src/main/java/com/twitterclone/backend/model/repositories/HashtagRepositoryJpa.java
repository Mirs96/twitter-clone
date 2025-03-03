package com.twitterclone.backend.model.repositories;

import com.twitterclone.backend.model.entities.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashtagRepositoryJpa extends JpaRepository<Hashtag, Long> {
    public Optional<Hashtag> findByTag(String tag);
}
