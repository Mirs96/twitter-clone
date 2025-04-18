package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.Follower;
import java.time.LocalDate;
import java.time.LocalTime;

public class FollowerUserDto {

    private long id;

    private long followerId;

    private String followerNickname;

    private String followerProfilePicture;

    LocalDate creationDate;

    LocalTime creationTime;

    public FollowerUserDto(long id, long followerId, String followerNickname, String followerProfilePicture, LocalDate creationDate, LocalTime creationTime) {
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
        this.creationDate = user.getCreationDate();
        this.creationTime = user.getCreationTime();
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

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalTime creationTime) {
        this.creationTime = creationTime;
    }
}

