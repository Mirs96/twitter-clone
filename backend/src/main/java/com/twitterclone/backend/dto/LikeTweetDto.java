package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.LikeTweet;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for creating a like on a tweet. Also used in responses for like details on a tweet.")
public class LikeTweetDto {

    @Schema(description = "Unique identifier of the like on the tweet", example = "502", accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @Schema(description = "Unique identifier of the user liking the tweet", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long userId;

    @Schema(description = "Unique identifier of the tweet being liked", example = "205", requiredMode = Schema.RequiredMode.REQUIRED)
    private long tweetId;

    public static LikeTweet fromDto(LikeTweetDto dto) {
        // This DTO is mainly for request. The service will construct the entity.
        return new LikeTweet();
    }
}