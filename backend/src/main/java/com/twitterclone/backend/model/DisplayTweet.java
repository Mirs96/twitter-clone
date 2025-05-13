package com.twitterclone.backend.model;

import com.twitterclone.backend.model.entities.Tweet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayTweet {

    private Tweet tweet;

    private long likeCount;

    private long replyCount;

    private long bookmarkCount;

    private boolean liked;

    private boolean bookmarked;

    private Long likeId;

    private Long bookmarkId;

    public DisplayTweet(Tweet tweet) {
        this.tweet = tweet;
        this.likeCount = 0;
        this.replyCount = 0;
        this.bookmarkCount = 0;
        this.liked = false;
        this.bookmarked = false;
        this.likeId = null;
        this.bookmarkId = null;
    }
}
