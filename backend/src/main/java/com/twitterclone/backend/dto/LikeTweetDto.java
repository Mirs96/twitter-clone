package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.LikeTweet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeTweetDto {

    private long id;

    private long userId;

    private long tweetId;

    public static LikeTweet fromDto(LikeTweetDto dto) {
        return new LikeTweet();
    }
}
