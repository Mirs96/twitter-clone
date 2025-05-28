package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.Hashtag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object representing a hashtag.")
public class HashtagDto {

    @Schema(description = "Unique identifier of the hashtag", example = "901")
    private long id;

    @Schema(description = "The hashtag text (without the '#')", example = "SpringBoot")
    private String tag;

    public HashtagDto(Hashtag h) {
        this.id = h.getId();
        this.tag = h.getTag();
    }
}