package com.twitterclone.backend.model;

import com.twitterclone.backend.model.entities.Reply;

public class DisplayReply {

    private Reply reply;

    private long likeCount;

    private boolean liked;

    private Long likeId;

    private boolean hasNestedReplies;

    public DisplayReply() {
    }

    public DisplayReply(Reply reply, long likeCount,  boolean liked, Long likeId, boolean hasNestedReplies) {
        this.reply = reply;
        this.likeCount = likeCount;
        this.liked = liked;
        this.likeId = likeId;
        this.hasNestedReplies = hasNestedReplies;
    }

    public DisplayReply(Reply reply) {
        this.reply = reply;
        this.likeCount = 0;
        this.liked = false;
        this.likeId = null;
        this.hasNestedReplies = false;
    }

    public Reply getReply() {
        return reply;
    }

    public void setReply(Reply reply) {
        this.reply = reply;
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
