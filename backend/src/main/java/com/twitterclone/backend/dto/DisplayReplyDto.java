package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.DisplayReply;
import io.micrometer.common.util.StringUtils;

public class DisplayReplyDto {

    private long id;

    private long userId;

    private String userNickname;

    private String userProfilePicture;

    private long tweetId;

    private Long parentReplyId;

    private String content;

    private String creationDate;

    private String creationTime;

    private long likeCount;

    private boolean liked;

    private Long likeId;

    private boolean hasNestedReplies;

    public DisplayReplyDto() {
    }

    public DisplayReplyDto(long id, long userId, String userNickname, String userProfilePicture, long tweetId, Long parentReplyId, String content, String creationDate, String creationTime, long likeCount, boolean liked, Long likeId, boolean hasNestedReplies) {
        this.id = id;
        this.userId = userId;
        this.userNickname = userNickname;
        this.userProfilePicture = userProfilePicture;
        this.tweetId = tweetId;
        this.parentReplyId = parentReplyId;
        this.content = content;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
        this.likeCount = likeCount;
        this.liked = liked;
        this.likeId = likeId;
        this.hasNestedReplies = hasNestedReplies;
    }

    public DisplayReplyDto(DisplayReply reply) {
        this.id = reply.getReply().getId();
        this.userId = reply.getReply().getUser().getId();
        this.userNickname = reply.getReply().getUser().getNickname();
        String profilePic = reply.getReply().getUser().getProfilePicture();
        this.userProfilePicture =  StringUtils.isBlank(profilePic) ? "/images/default-avatar.png" : profilePic;
        this.tweetId = reply.getReply().getTweet().getId();
        this.parentReplyId = (reply.getReply().getParentReply() != null) ? reply.getReply().getParentReply().getId() : null;
        this.content = reply.getReply().getContent();
        this.creationDate = reply.getReply().getCreationDate().toString();
        this.creationTime = reply.getReply().getCreationTime().toString();
        this.likeCount = reply.getLikeCount();
        this.liked = reply.isLiked();
        this.likeId = reply.getLikeId();
        this.hasNestedReplies = reply.isHasNestedReplies();
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

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }

    public long getTweetId() {
        return tweetId;
    }

    public void setTweetId(long tweetId) {
        this.tweetId = tweetId;
    }

    public Long getParentReplyId() {
        return parentReplyId;
    }

    public void setParentReplyId(Long parentReplyId) {
        this.parentReplyId = parentReplyId;
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

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public Long getLikeId() {
        return likeId;
    }

    public void setLikeId(Long likeId) {
        this.likeId = likeId;
    }

    public boolean isHasNestedReplies() {
        return hasNestedReplies;
    }

    public void setHasNestedReplies(boolean hasNestedReplies) {
        this.hasNestedReplies = hasNestedReplies;
    }
}
