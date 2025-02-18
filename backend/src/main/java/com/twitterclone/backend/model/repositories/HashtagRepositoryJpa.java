package com.twitterclone.backend.model.repositories;

import com.twitterclone.backend.model.entities.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashtagRepositoryJpa extends JpaRepository<Hashtag, Long> {
}
