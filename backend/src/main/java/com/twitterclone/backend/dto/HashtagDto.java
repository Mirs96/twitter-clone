package com.twitterclone.backend.dto;

import com.twitterclone.backend.model.entities.Hashtag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HashtagDto {
    private long id;
    private String tag;

    public HashtagDto(Hashtag h) {
        this.id = h.getId();
        this.tag = h.getTag();
    }
}
