package com.twitterclone.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    private String nickname;
    private String profilePicture;
    private String bio;
    private long followersCount;
    private long followingCount;
    private boolean isFollowing;
}
