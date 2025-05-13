package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.Tweet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTweetDto {
    private long id;

    private long userId;

    private String userNickname;

    private String content;

    private String createdAt;

    public static Tweet fromDto(CreateTweetDto dto) {
        return Tweet.builder()
                .content(dto.getContent())
                .build();
    }
}