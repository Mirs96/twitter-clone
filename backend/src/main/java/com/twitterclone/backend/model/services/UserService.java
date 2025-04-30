package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.UserProfile;
import com.twitterclone.backend.model.entities.Follower;
import com.twitterclone.backend.model.entities.User;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.exceptions.ReactionAlreadyExistsException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface UserService {
    Optional<User> findById(Long userId);

    UserProfile getProfile(long profileUserId, long currentUserId) throws EntityNotFoundException;
    void updateUserProfile(long userId, String bio, String avatarFilename) throws EntityNotFoundException;

    Page<Follower> findFollowersByUserId(long userId, Pageable pageable) throws EntityNotFoundException;
    Page<Follower> findFollowingByFollowerId(long followerId, Pageable pageable) throws EntityNotFoundException;

    void followUser(long followerId, long userId) throws EntityNotFoundException, ReactionAlreadyExistsException;
    void unfollowUser(long followerId, long userToFollowId) throws EntityNotFoundException;
}
