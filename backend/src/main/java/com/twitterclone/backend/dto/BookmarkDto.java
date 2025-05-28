package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.Bookmark;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for creating a bookmark. Also used in responses for bookmark details.")
public class BookmarkDto {

    @Schema(description = "Unique identifier of the bookmark", example = "101", accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @Schema(description = "Unique identifier of the user creating the bookmark", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long userId;

    @Schema(description = "Unique identifier of the tweet being bookmarked", example = "202", requiredMode = Schema.RequiredMode.REQUIRED)
    private long tweetId;

    @Schema(description = "Timestamp of when the bookmark was created", example = "2023-10-27T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private String createdAt; // Assuming this will be formatted string in response

    public static Bookmark fromDto(BookmarkDto b) {
        // This DTO is mainly for request. The service will construct the entity.
        return new Bookmark();
    }
}