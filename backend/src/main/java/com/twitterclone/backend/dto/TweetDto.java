package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.Tweet;
import com.twitterclone.backend.model.entities.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TweetDto {
    private long id;

    private long userId;

    private String userNickname;

    private String content;

    private String creationDate;

    private String creationTime;

    public TweetDto() {
    }


    public TweetDto(long id, long userId, String userNickname, String content, String creationDate, String creationTime) {
        this.id = id;
        this.userId = userId;
        this.userNickname = userNickname;
        this.content = content;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
    }

    public TweetDto(Tweet tweet) {
        this.id = tweet.getId();
        this.userId = tweet.getUser().getId();
        this.userNickname = tweet.getUser().getNickname();
        this.content = tweet.getContent();
        this.creationDate = tweet.getCreationDate().toString();
        this.creationTime = tweet.getCreationTime().toString();
    }

    public static Tweet fromDto(TweetDto dto) {
        return new Tweet(dto.getId(),
                null,
                dto.getContent(),
                LocalDate.parse(dto.getCreationDate(), DateTimeFormatter.ISO_LOCAL_DATE),
                LocalTime.parse(dto.getCreationTime(), DateTimeFormatter.ISO_LOCAL_TIME));
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
}