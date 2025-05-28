package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.DisplayReply;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for displaying a reply with additional computed information.")
public class DisplayReplyDto {

    @Schema(description = "Unique identifier of the reply", example = "303")
    private long id;

    @Schema(description = "Unique identifier of the user who created the reply", example = "1")
    private long userId;

    @Schema(description = "Nickname of the user who created the reply", example = "johnnyD")
    private String userNickname;

    @Schema(description = "URL or path to the profile picture of the user who created the reply", example = "/uploads/avatars/johnnyD.png")
    private String userProfilePicture;

    @Schema(description = "Unique identifier of the tweet this reply belongs to", example = "202")
    private long tweetId;

    @Schema(description = "Unique identifier of the parent reply, if this is a nested reply", example = "301")
    private Long parentReplyId;

    @Schema(description = "Content of the reply", example = "Great point!")
    private String content;

    @Schema(description = "Timestamp of when the reply was created", example = "2023-10-27T11:00:00")
    private String createdAt;

    @Schema(description = "Number of likes this reply has received", example = "15")
    private long likeCount;

    @Schema(description = "Indicates if the current authenticated user has liked this reply", example = "true")
    private boolean liked;

    @Schema(description = "Identifier of the like by the current user on this reply, if liked", example = "501")
    private Long likeId;

    @Schema(description = "Indicates if this reply has nested replies", example = "false")
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