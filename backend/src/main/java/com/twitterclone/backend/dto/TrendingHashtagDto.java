package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.TrendingHashtag;
// import com.twitterclone.backend.model.entities.Hashtag; // Not directly used, but for context
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object representing a trending hashtag with its usage count.")
public class TrendingHashtagDto {

    @Schema(description = "Unique identifier of the hashtag", example = "901")
    private long id;

    @Schema(description = "The hashtag text (without the '#')", example = "SpringBoot")
    private String tag;

    @Schema(description = "The count of how many times this hashtag has been used (e.g., in tweets)", example = "1500")
    private long count;

    public TrendingHashtagDto(TrendingHashtag trendingHashtag) {
        this.id = trendingHashtag.getHashtag().getId();
        this.tag = trendingHashtag.getHashtag().getTag();
        this.count = trendingHashtag.getCount();
    }
}