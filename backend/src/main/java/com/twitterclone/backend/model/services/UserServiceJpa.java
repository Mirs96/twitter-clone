package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.UserProfile;
import com.twitterclone.backend.model.entities.*;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.exceptions.ReactionAlreadyExistsException;
import com.twitterclone.backend.model.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
public class UserServiceJpa implements UserService {
    private UserRepositoryJpa userRepo;
    private FollowerRepositoryJpa followerRepo;

    @Autowired
    public UserServiceJpa(UserRepositoryJpa userRepo, FollowerRepositoryJpa followerRepo) {
        this.userRepo = userRepo;
        this.followerRepo = followerRepo;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userRepo.findById(userId);
    }

    @Override
    public Page<Follower> findFollowersByUserId(long userId, Pageable pageable) throws EntityNotFoundException {
        userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));

        return followerRepo.findByUserId(userId, pageable);
    }

    @Override
    public Page<Follower> findFollowingByFollowerId(long followerId, Pageable pageable) throws EntityNotFoundException {
        userRepo.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));

        return followerRepo.findByFollowerId(followerId, pageable);
    }

    @Override
    public void followUser(long followerId, long userId) throws EntityNotFoundException, ReactionAlreadyExistsException {
        User follower = userRepo.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));

        if (followerId == userId) {
            throw new ReactionAlreadyExistsException("A user cannot follow themselves", Follower.class.getName());
        }

        if (followerRepo.findByFollowerAndUser(followerId, userId).isPresent()) {
            throw new ReactionAlreadyExistsException("Already following", Follower.class.getName());
        }

        Follower f = new Follower();
        f.setUser(user);
        f.setFollower(follower);
        f.setCreationDate(LocalDate.now());
        f.setCreationTime(LocalTime.now());

        followerRepo.save(f);
    }

    @Override
    public void unfollowUser(long followerId, long userToFollowId) throws EntityNotFoundException {

        User follower = userRepo.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Follower: Entity not found", User.class.getName()));

        User followed = userRepo.findById(userToFollowId)
                .orElseThrow(() -> new EntityNotFoundException("Followed: Entity not found", User.class.getName()));

        Follower f = followerRepo.findByFollowerAndUser(followerId, userToFollowId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", Follower.class.getName()));

        followerRepo.delete(f);
    }

    @Override
    public UserProfile getProfile(long profileUserId, long currentUserId) throws EntityNotFoundException {
        User user = userRepo.findById(profileUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found", User.class.getName()));

        long followersCount = followerRepo.countByUserId(profileUserId);
        long followingCount = followerRepo.countByFollowerId(profileUserId);

        Optional<Follower> of = followerRepo.findByFollowerAndUser(currentUserId, profileUserId);

        String profilePicture = user.getProfilePicture();

        if (profilePicture == null || profilePicture.isEmpty()) {
            profilePicture = "/images/default-avatar.png";
        }

        return new UserProfile(
                user.getNickname(),
                profilePicture,
                user.getBio(),
                followersCount,
                followingCount,
                of.isEmpty() ? false : true
        );
    }

    @Override
    public void updateUserProfile(long userId, String bio, String avatarFilename) throws EntityNotFoundException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));

        if (bio != null) {
            user.setBio(bio);
        }
        if (avatarFilename != null) {
            user.setProfilePicture("/uploads/avatars/" + avatarFilename);
        }

        userRepo.save(user);
    }
}
