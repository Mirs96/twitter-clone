package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.Bookmark;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BookmarkDto {

    private long id;

    private long userId;

    private long tweetId;

    private String creationDate;

    private String creationTime;

    public BookmarkDto() {
    }

    public BookmarkDto(long id, long userId, long tweetId, String creationDate, String creationTime) {
        this.id = id;
        this.userId = userId;
        this.tweetId = tweetId;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
    }

    public static Bookmark fromDto(BookmarkDto b) {
        return new Bookmark(
                b.getId(),
                null,
                null,
                LocalDate.parse(b.getCreationDate()),
                LocalTime.parse(b.getCreationTime())
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
