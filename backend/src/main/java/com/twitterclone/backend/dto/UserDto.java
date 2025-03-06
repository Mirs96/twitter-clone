package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.User;

public class UserDto {
    private Long id;
    private String nickname;
    private String profilePicture;

    public UserDto() {
    }

    public UserDto(Long id, String nickname, String profilePicture) {
        this.id = id;
        this.nickname = nickname;
        this.profilePicture = profilePicture;
    }

    public UserDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.profilePicture = user.getProfilePicture();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
