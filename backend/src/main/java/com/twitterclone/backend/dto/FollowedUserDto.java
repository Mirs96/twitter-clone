package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.Follower;

public class FollowedUserDto {

    private long id;

    private long userId;

    private String userNickname;

    private String userProfilePicture;

    private String creationDate;

    private String creationTime;

    public FollowedUserDto(long id, long userId, String userNickname, String userProfilePicture, String creationDate, String creationTime) {
        this.id = id;
        this.userId = userId;
        this.userNickname = userNickname;
        this.userProfilePicture = userProfilePicture;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
    }

    public FollowedUserDto(Follower user) {
        this.id = user.getId();
        this.userId = user.getUser().getId();
        this.userNickname = user.getUser().getNickname();
        this.userProfilePicture = user.getUser().getProfilePicture();
        this.creationDate = user.getCreationDate().toString();
        this.creationTime = user.getCreationTime().toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
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
