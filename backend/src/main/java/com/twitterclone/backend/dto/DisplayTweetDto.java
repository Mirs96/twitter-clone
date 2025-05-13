package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.DisplayTweet;
import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayTweetDto {

    private long id;

    private long userId;

    private String userNickname;

    private String userProfilePicture;

    private String content;

    private String createdAt;

    private long likeCount;

    private long replyCount;

    private long bookmarkCount;

    private boolean liked;

    private boolean bookmarked;

    private Long likeId;

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
