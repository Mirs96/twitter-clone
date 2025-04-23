package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.Follower;

public class FollowerUserDto {

    private long id;

    private long followerId;

    private String followerNickname;

    private String followerProfilePicture;

    private String creationDate;

    private String creationTime;

    public FollowerUserDto(long id, long followerId, String followerNickname, String followerProfilePicture, String creationDate, String creationTime) {
        this.id = id;
        this.followerId = followerId;
        this.followerNickname = followerNickname;
        this.followerProfilePicture = followerProfilePicture;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
    }

    public FollowerUserDto(Follower user) {
        this.id = user.getId();
        this.followerId = user.getFollower().getId();
        this.followerNickname = user.getFollower().getNickname();
        this.followerProfilePicture = user.getFollower().getProfilePicture();
        this.creationDate = user.getCreationDate().toString();
        this.creationTime = user.getCreationTime().toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFollowerId() {
        return followerId;
    }

    public void setFollowerId(long followerId) {
        this.followerId = followerId;
    }

    public String getFollowerNickname() {
        return followerNickname;
    }

    public void setFollowerNickname(String followerNickname) {
        this.followerNickname = followerNickname;
    }

    public String getFollowerProfilePicture() {
        return followerProfilePicture;
    }

    public void setFollowerProfilePicture(String followerProfilePicture) {
        this.followerProfilePicture = followerProfilePicture;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }
}

