package com.twitterclone.backend.model;

import com.twitterclone.backend.model.entities.Tweet;

public class DisplayTweet {

    private Tweet tweet;

    private long likeCount;

    private long replyCount;

    private long bookmarkCount;

    private boolean liked;

    private boolean bookmarked;

    private Long likeId;

    private Long bookmarkId;

    public DisplayTweet() {
    }

    public DisplayTweet(Tweet tweet, long likeCount, long replyCount, long bookmarkCount, boolean liked, boolean bookmarked, Long likeId, Long bookmarkId) {
        this.tweet = tweet;
        this.likeCount = likeCount;
        this.replyCount = replyCount;
        this.bookmarkCount = bookmarkCount;
        this.liked = liked;
        this.bookmarked = bookmarked;
        this.likeId = likeId;
        this.bookmarkId = bookmarkId;
    }

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

    public Tweet getTweet() {
        return tweet;
    }

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
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

    public void setBookmarkCount(long bookmarkCount) {
        this.bookmarkCount = bookmarkCount;
    }

    public Long getLikeId() {
        return likeId;
    }

    public void setLikeId(Long likeId) {
        this.likeId = likeId;
    }

    public Long getBookmarkId() {
        return bookmarkId;
    }

    public void setBookmarkId(Long bookmarkId) {
        this.bookmarkId = bookmarkId;
    }
}
