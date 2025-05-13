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
public class CreateReplyDto {

    private long id;

    private long userId;

    private long tweetId;

    private Long parentReplyId;

    private String content;

    private String createdAt;


    public static Reply fromDto(CreateReplyDto dto) {
        return Reply.builder()
                .content(dto.getContent())
                .build();
    }
}
