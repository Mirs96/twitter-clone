package com.twitterclone.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twitterclone.backend.model.UserProfile;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private String nickname;

    private String profilePicture;

    private String bio;

    private long followersCount;

    private long followingCount;

    @JsonProperty("isFollowing")
    private boolean isFollowing;

    public UserProfileDto(UserProfile profile) {
        this.nickname = profile.getNickname();
        this.profilePicture = StringUtils.isBlank(profile.getProfilePicture()) ? "/images/default-avatar.png" : profile.getProfilePicture();
        this.bio = profile.getBio();
        this.followersCount = profile.getFollowersCount();
        this.followingCount = profile.getFollowingCount();
        this.isFollowing = profile.isFollowing();
    }
}
