package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.LikeTweet;

public class LikeTweetDto {
    private long id;

    private long userId;

    private long tweetId;

    public LikeTweetDto() {
    }

    public LikeTweetDto(long id, long userId, long tweetId) {
        this.id = id;
        this.userId = userId;
        this.tweetId = tweetId;
    }

    public static LikeTweet fromDto(LikeTweetDto dto) {
        return new LikeTweet(
                dto.getId(),
                null,
                null
        );
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getTweetId() {
        return tweetId;
    }

    public void setTweetId(long tweetId) {
        this.tweetId = tweetId;
    }
}
