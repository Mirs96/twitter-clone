package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.User;
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
@Schema(description = "Basic Data Transfer Object representing a user, typically for lists or brief mentions.")
public class UserDto {

    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;

    @Schema(description = "Nickname of the user", example = "johnnyD")
    private String nickname;

    @Schema(description = "URL or path to the user's profile picture", example = "/uploads/avatars/johnnyD.png")
    private String profilePicture;

    public UserDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.profilePicture = StringUtils.isBlank(user.getProfilePicture()) ? "/images/default-avatar.png" : user.getProfilePicture();
    }
}