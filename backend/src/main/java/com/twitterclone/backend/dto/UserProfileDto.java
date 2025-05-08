package com.twitterclone.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twitterclone.backend.model.UserProfile;

public class UserProfileDto {
    private String nickname;

    private String profilePicture;

    private String bio;

    private long followersCount;

    private long followingCount;

    @JsonProperty("isFollowing")
    private boolean isFollowing;

    public UserProfileDto(String nickname, String profilePicture, String bio, long followersCount, long followingCount, boolean isFollowing) {
        this.nickname = nickname;
        this.profilePicture = profilePicture;
        this.bio = bio;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.isFollowing = isFollowing;
    }

    public UserProfileDto(UserProfile profile) {
        this.nickname = profile.getNickname();
        this.profilePicture = profile.getProfilePicture();
        this.bio = profile.getBio();
        this.followersCount = profile.getFollowersCount();
        this.followingCount = profile.getFollowingCount();
        this.isFollowing = profile.isFollowing();
    }

    public UserProfileDto() {
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public long getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(long followersCount) {
        this.followersCount = followersCount;
    }

    public long getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(long followingCount) {
        this.followingCount = followingCount;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }
}
