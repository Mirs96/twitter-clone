package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.entities.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findById(Long userId);
}
