package com.twitterclone.backend.model;

import com.twitterclone.backend.model.entities.Hashtag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendingHashtag {
    Hashtag hashtag;
    private long count;
}