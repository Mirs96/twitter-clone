package com.twitterclone.backend.model;

import com.twitterclone.backend.model.entities.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisplayReply {

    private Reply reply;

    private long likeCount;

    private boolean liked;

    private Long likeId;

    private boolean hasNestedReplies;

    public DisplayReply(Reply reply) {
        this.reply = reply;
        this.likeCount = 0;
        this.liked = false;
        this.likeId = null;
        this.hasNestedReplies = false;
    }
}
