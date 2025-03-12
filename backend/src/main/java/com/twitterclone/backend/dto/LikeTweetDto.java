package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.LikeTweet;

public class LikeTweetDto {
    private long id;

    private long userId;

    private String userNickname;

    private long tweetId;

    private String emoji;

    public LikeTweetDto() {
    }

    public LikeTweetDto(long id, long userId, String userNickname, long tweetId, String emoji) {
        this.id = id;
        this.userId = userId;
        this.userNickname = userNickname;
        this.tweetId = tweetId;
        this.emoji = emoji;
    }

    public static LikeTweet fromDto(LikeTweetDto dto) {
        return new LikeTweet(
                dto.getId(),
                null,
                null,
                dto.getEmoji()
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

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public long getTweetId() {
        return tweetId;
    }

    public void setTweetId(long tweetId) {
        this.tweetId = tweetId;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}
