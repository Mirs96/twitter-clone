package com.twitterclone.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.twitterclone.backend.model.UserProfile;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object representing a detailed user profile.")
public class UserProfileDto {

    @Schema(description = "Nickname of the user", example = "johnnyD")
    private String nickname;

    @Schema(description = "URL or path to the user's profile picture", example = "/uploads/avatars/johnnyD.png")
    private String profilePicture;

    @Schema(description = "Biography of the user", example = "Loves coding and coffee.")
    private String bio;

    @Schema(description = "Number of users following this user", example = "150")
    private long followersCount;

    @Schema(description = "Number of users this user is following", example = "75")
    private long followingCount;

    @JsonProperty("isFollowing") // Keep existing Jackson annotation
    @Schema(description = "Indicates if the current authenticated user is following this profile's user", example = "true")
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