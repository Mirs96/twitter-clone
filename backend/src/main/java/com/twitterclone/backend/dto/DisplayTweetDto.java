package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.DisplayTweet;
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
@Schema(description = "Data Transfer Object for displaying a tweet with additional computed information.")
public class DisplayTweetDto {

    @Schema(description = "Unique identifier of the tweet", example = "205")
    private long id;

    @Schema(description = "Unique identifier of the user who created the tweet", example = "1")
    private long userId;

    @Schema(description = "Nickname of the user who created the tweet", example = "johnnyD")
    private String userNickname;

    @Schema(description = "URL or path to the profile picture of the user who created the tweet", example = "/uploads/avatars/johnnyD.png")
    private String userProfilePicture;

    @Schema(description = "Content of the tweet", example = "Hello world! #newbeginnings")
    private String content;

    @Schema(description = "Timestamp of when the tweet was created", example = "2023-10-27T12:00:00")
    private String createdAt;

    @Schema(description = "Number of likes this tweet has received", example = "120")
    private long likeCount;

    @Schema(description = "Number of replies this tweet has received", example = "25")
    private long replyCount;

    @Schema(description = "Number of times this tweet has been bookmarked", example = "10")
    private long bookmarkCount;

    @Schema(description = "Indicates if the current authenticated user has liked this tweet", example = "true")
    private boolean liked;

    @Schema(description = "Indicates if the current authenticated user has bookmarked this tweet", example = "false")
    private boolean bookmarked;

    @Schema(description = "Identifier of the like by the current user on this tweet, if liked", example = "502")
    private Long likeId;

    @Schema(description = "Identifier of the bookmark by the current user on this tweet, if bookmarked", example = "102")
    private Long bookmarkId;

    public DisplayTweetDto(DisplayTweet tweet) {
        this.id = tweet.getTweet().getId();
        this.userId = tweet.getTweet().getUser().getId();
        this.userNickname = tweet.getTweet().getUser().getNickname();
        String profilePic = tweet.getTweet().getUser().getProfilePicture();
        this.userProfilePicture =  StringUtils.isBlank(profilePic) ? "/images/default-avatar.png" : profilePic;
        this.content = tweet.getTweet().getContent();
        this.createdAt = tweet.getTweet().getCreatedAt().toString();
        this.likeCount = tweet.getLikeCount();
        this.replyCount = tweet.getReplyCount();
        this.bookmarkCount = tweet.getBookmarkCount();
        this.liked = tweet.isLiked();
        this.bookmarked = tweet.isBookmarked();
        this.likeId = tweet.getLikeId();
        this.bookmarkId = tweet.getBookmarkId();
    }
}