package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.LikeReply;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for creating a like on a reply. Also used in responses for like details on a reply.")
public class LikeReplyDto {

    @Schema(description = "Unique identifier of the like on the reply", example = "501", accessMode = Schema.AccessMode.READ_ONLY)
    private long id;

    @Schema(description = "Unique identifier of the user liking the reply", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long userId;

    @Schema(description = "Unique identifier of the reply being liked", example = "303", requiredMode = Schema.RequiredMode.REQUIRED)
    private long replyId;

    public static LikeReply fromDto(LikeReplyDto dto) {
        // This DTO is mainly for request. The service will construct the entity.
        return new LikeReply();
    }
}