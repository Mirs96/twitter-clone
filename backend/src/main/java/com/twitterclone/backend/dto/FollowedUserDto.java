package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.Follower;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object representing a user that the current user is following.")
public class FollowedUserDto {

    @Schema(description = "Unique identifier of the follow relationship", example = "701")
    private long id; // This is the ID of the Follower entity

    @Schema(description = "Unique identifier of the user being followed", example = "2")
    private long userId;

    @Schema(description = "Nickname of the user being followed", example = "janeDoe")
    private String userNickname;

    @Schema(description = "URL or path to the profile picture of the user being followed", example = "/uploads/avatars/janeDoe.png")
    private String userProfilePicture;

    @Schema(description = "Timestamp of when the follow relationship was established", example = "2023-10-26T09:00:00")
    private String createdAt;

    public FollowedUserDto(Follower followerRelationship) { // Renamed parameter for clarity
        this.id = followerRelationship.getId();
        this.userId = followerRelationship.getUser().getId(); // The 'user' in Follower entity is the one being followed
        this.userNickname = followerRelationship.getUser().getNickname();
        this.userProfilePicture = followerRelationship.getUser().getProfilePicture();
        this.createdAt = followerRelationship.getCreatedAt().toString();
    }
}