package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.TrendingHashtag;
import com.twitterclone.backend.model.entities.Hashtag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendingHashtagDto {
    private long id;
    private String tag;
    private long count;

    public TrendingHashtagDto(TrendingHashtag trendingHashtag) {
        this.id = trendingHashtag.getHashtag().getId();
        this.tag = trendingHashtag.getHashtag().getTag();
        this.count = trendingHashtag.getCount();
    }
}