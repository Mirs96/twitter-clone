package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplyDto {

    private long id;

    private long userId;

    private String userNickname;

    private String userProfilePicture;

    private long tweetId;

    private Long parentReplyId;

    private String content;

    private String createdAt;

    public ReplyDto(Reply reply) {
        this.id = reply.getId();
        this.userId = reply.getUser().getId();
        this.userNickname = reply.getUser().getNickname();
        this.userProfilePicture = reply.getUser().getProfilePicture();
        this.tweetId = reply.getTweet().getId();
        this.parentReplyId = (reply.getParentReply() != null) ? reply.getParentReply().getId() : null;
        this.content = reply.getContent();
        this.createdAt = reply.getCreatedAt().toString();
    }
}
