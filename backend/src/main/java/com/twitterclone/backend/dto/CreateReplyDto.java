package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.Reply;

import java.time.LocalDate;
import java.time.LocalTime;

public class CreateReplyDto {

    private long id;

    private long userId;

    private long tweetId;

    private Long parentReplyId;

    private String content;

    private String creationDate;

    private String creationTime;

    public CreateReplyDto(long id, long userId, long tweetId, Long parentReplyId, String content, String creationDate, String creationTime) {
        this.id = id;
        this.userId = userId;
        this.tweetId = tweetId;
        this.parentReplyId = parentReplyId;
        this.content = content;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
    }

    public static Reply fromDto(CreateReplyDto dto) {
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
