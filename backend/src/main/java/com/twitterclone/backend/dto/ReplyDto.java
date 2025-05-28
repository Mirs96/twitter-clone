package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.Reply;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Basic Data Transfer Object for representing a reply. (Consider using DisplayReplyDto for richer responses).")
public class ReplyDto {

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

    public ReplyDto(Reply reply) {
        this.id = reply.getId();
        this.userId = reply.getUser().getId();
        this.userNickname = reply.getUser().getNickname();
        this.userProfilePicture = reply.getUser().getProfilePicture(); // Assuming default handling is done elsewhere or not needed here
        this.tweetId = reply.getTweet().getId();
        this.parentReplyId = (reply.getParentReply() != null) ? reply.getParentReply().getId() : null;
        this.content = reply.getContent();
        this.createdAt = reply.getCreatedAt().toString();
    }
}