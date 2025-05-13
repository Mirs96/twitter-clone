package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.DisplayReply;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayReplyDto {

    private long id;

    private long userId;

    private String userNickname;

    private String userProfilePicture;

    private long tweetId;

    private Long parentReplyId;

    private String content;

    private String createdAt;

    private long likeCount;

    private boolean liked;

    private Long likeId;

    private boolean hasNestedReplies;

    public DisplayReplyDto(DisplayReply reply) {
        this.id = reply.getReply().getId();
        this.userId = reply.getReply().getUser().getId();
        this.userNickname = reply.getReply().getUser().getNickname();
        String profilePic = reply.getReply().getUser().getProfilePicture();
        this.userProfilePicture =  StringUtils.isBlank(profilePic) ? "/images/default-avatar.png" : profilePic;
        this.tweetId = reply.getReply().getTweet().getId();
        this.parentReplyId = (reply.getReply().getParentReply() != null) ? reply.getReply().getParentReply().getId() : null;
        this.content = reply.getReply().getContent();
        this.createdAt = reply.getReply().getCreatedAt().toString();
        this.likeCount = reply.getLikeCount();
        this.liked = reply.isLiked();
        this.likeId = reply.getLikeId();
        this.hasNestedReplies = reply.isHasNestedReplies();
    }
}
