package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.UserProfile;

public class UserProfileDto {
    private String nickname;
    private long followersCount;
    private long followingCount;
    private boolean isFollowing;

    public UserProfileDto(String nickname, long followersCount, long followingCount, boolean isFollowing) {
        this.nickname = nickname;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.isFollowing = isFollowing;
    }

    public UserProfileDto(UserProfile profile) {
        this.nickname = profile.getNickname();
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
