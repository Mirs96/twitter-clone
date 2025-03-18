package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.Reply;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReplyDto {

    private long id;

    private long userId;

    private String userNickname;

    private String userProfilePicture;

    private long tweetId;

    private Long parentReplyId;

    private String content;

    private String creationDate;

    private String creationTime;

    public ReplyDto() {
    }

    public ReplyDto(long id, long userId, String userNickname, String userProfilePicture, long tweetId, Long parentReplyId, String content, String creationDate, String creationTime) {
        this.id = id;
        this.userId = userId;
        this.userNickname = userNickname;
        this.userProfilePicture = userProfilePicture;
        this.tweetId = tweetId;
        this.parentReplyId = parentReplyId;
        this.content = content;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
    }

    public ReplyDto(Reply reply) {
        this.id = reply.getId();
        this.userId = reply.getUser().getId();
        this.userNickname = reply.getUser().getNickname();
        this.userProfilePicture = reply.getUser().getProfilePicture();
        this.tweetId = reply.getTweet().getId();
        this.parentReplyId = (reply.getParentReply() != null) ? reply.getParentReply().getId() : null;         this.content = reply.getContent();
        this.creationDate = reply.getCreationDate().toString();
        this.creationTime = reply.getCreationTime().toString();
    }

    public static Reply fromDto(ReplyDto dto) {
        return new Reply(
                dto.getId(),
                null,
                null,
                null,
                dto.getContent(),
                LocalDate.parse(dto.getCreationDate()),
                LocalTime.parse(dto.getCreationTime())
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
}
