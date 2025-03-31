package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.LikeReply;
import com.twitterclone.backend.model.entities.LikeTweet;

public class LikeReplyDto {

    private long id;

    private long userId;

    private long replyId;

    public LikeReplyDto() {
    }

    public LikeReplyDto(long id, long userId, long replyId) {
        this.id = id;
        this.userId = userId;
        this.replyId = replyId;
    }

    public static LikeReply fromDto(LikeReplyDto dto) {
        return new LikeReply(
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

    public long getReplyId() {
        return replyId;
    }

    public void setReplyId(long replyId) {
        this.replyId = replyId;
    }
}
