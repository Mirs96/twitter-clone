package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.Follower;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowedUserDto {

    private long id;

    private long userId;

    private String userNickname;

    private String userProfilePicture;

    private String createdAt;

    public FollowedUserDto(Follower user) {
        this.id = user.getId();
        this.userId = user.getUser().getId();
        this.userNickname = user.getUser().getNickname();
        this.userProfilePicture = user.getUser().getProfilePicture();
        this.createdAt = user.getCreatedAt().toString();
    }
}
