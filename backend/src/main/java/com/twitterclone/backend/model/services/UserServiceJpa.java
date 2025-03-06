package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.entities.User;
import com.twitterclone.backend.model.repositories.UserRepositoryJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceJpa implements UserService {
    private UserRepositoryJpa userRepo;

    @Autowired
    public UserServiceJpa(UserRepositoryJpa userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userRepo.findById(userId);
    }
}
