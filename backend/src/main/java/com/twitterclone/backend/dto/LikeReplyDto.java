package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.LikeReply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeReplyDto {

    private long id;

    private long userId;

    private long replyId;

    public static LikeReply fromDto(LikeReplyDto dto) {
        return new LikeReply();
    }
}
