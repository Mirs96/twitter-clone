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
@Schema(description = "Data Transfer Object for creating a new reply.")
public class CreateReplyDto {

    @Schema(description = "Unique identifier of the reply (generated by server, returned in response)", example = "303", accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @Schema(description = "Unique identifier of the user creating the reply", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long userId;

    @Schema(description = "Unique identifier of the tweet being replied to", example = "202", requiredMode = Schema.RequiredMode.REQUIRED)
    private long tweetId;

    @Schema(description = "Unique identifier of the parent reply, if this is a nested reply (optional)", example = "301")
    private Long parentReplyId;

    @Schema(description = "Content of the reply", example = "Great point!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "Timestamp of when the reply was created (generated by server, returned in response)", example = "2023-10-27T11:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private String createdAt; // Assuming this will be formatted string in response

    public static Reply fromDto(CreateReplyDto dto) {
        return Reply.builder()
                .content(dto.getContent())
                .build();
    }
}