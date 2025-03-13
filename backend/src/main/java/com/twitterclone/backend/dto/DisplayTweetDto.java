package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.DisplayTweet;

public class DisplayTweetDto {

    private long id;

    private long userId;

    private String userNickname;

    private String content;

    private String creationDate;

    private String creationTime;

    private long likeCount;

    private long replyCount;

    private long bookmarkCount;

    private boolean liked;

    private boolean bookmarked;

    public DisplayTweetDto() {
    }

    public DisplayTweetDto(long id, long userId, String userNickname, String content, String creationDate, String creationTime, long likeCount, long replyCount, long bookmarkCount, boolean liked, boolean bookmarked) {
        this.id = id;
        this.userId = userId;
        this.userNickname = userNickname;
        this.content = content;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.bookmarkCount = bookmarkCount;
        this.liked = liked;
        this.bookmarked = bookmarked;
    }

    public DisplayTweetDto(DisplayTweet tweet) {
        this.id = tweet.getTweet().getId();
        this.userId = tweet.getTweet().getUser().getId();
        this.userNickname = tweet.getTweet().getUser().getNickname();
        this.content = tweet.getTweet().getContent();
        this.creationDate = tweet.getTweet().getCreationDate().toString();
        this.creationTime = tweet.getTweet().getCreationTime().toString();
        this.likeCount = tweet.getLikeCount();
        this.replyCount = tweet.getReplyCount();
        this.bookmarkCount = tweet.getBookmarkCount();
        this.liked = tweet.isLiked();
        this.bookmarked = tweet.isBookmarked();
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public long getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(long replyCount) {
        this.replyCount = replyCount;
    }

    public long getBookmarkCount() {
        return bookmarkCount;
    }

    public void setBookmarkCount(long bookmarkCount) {
        this.bookmarkCount = bookmarkCount;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }
}
