package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDto {

    private long id;
    private long userId;
    private long tweetId;
    private String createdAt;

    public static Bookmark fromDto(BookmarkDto b) {
        return new Bookmark();
    }
}
