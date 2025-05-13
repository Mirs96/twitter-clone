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
public class FollowerUserDto {

    private long id;

    private long followerId;

    private String followerNickname;

    private String followerProfilePicture;

    private String createdAt;

    public FollowerUserDto(Follower user) {
        this.id = user.getId();
        this.followerId = user.getFollower().getId();
        this.followerNickname = user.getFollower().getNickname();
        this.followerProfilePicture = user.getFollower().getProfilePicture();
        this.createdAt = user.getCreatedAt().toString();
    }
}

