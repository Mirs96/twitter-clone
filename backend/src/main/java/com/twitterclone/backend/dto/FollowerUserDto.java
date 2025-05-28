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
@Schema(description = "Data Transfer Object representing a user who is following the current user.")
public class FollowerUserDto {

    @Schema(description = "Unique identifier of the follow relationship", example = "702")
    private long id; // This is the ID of the Follower entity

    @Schema(description = "Unique identifier of the user who is following", example = "3")
    private long followerId;

    @Schema(description = "Nickname of the user who is following", example = "anotherUser")
    private String followerNickname;

    @Schema(description = "URL or path to the profile picture of the user who is following", example = "/uploads/avatars/anotherUser.png")
    private String followerProfilePicture;

    @Schema(description = "Timestamp of when the follow relationship was established", example = "2023-10-25T14:00:00")
    private String createdAt;

    public FollowerUserDto(Follower followerRelationship) { // Renamed parameter for clarity
        this.id = followerRelationship.getId();
        this.followerId = followerRelationship.getFollower().getId(); // The 'follower' in Follower entity is the one initiating the follow
        this.followerNickname = followerRelationship.getFollower().getNickname();
        this.followerProfilePicture = followerRelationship.getFollower().getProfilePicture();
        this.createdAt = followerRelationship.getCreatedAt().toString();
    }
}