package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.User;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String nickname;
    private String profilePicture;

    public UserDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.profilePicture = StringUtils.isBlank(user.getProfilePicture()) ? "/images/default-avatar.png" : user.getProfilePicture();
    }
}
