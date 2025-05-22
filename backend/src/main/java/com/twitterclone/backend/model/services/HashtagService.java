package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.TrendingHashtag;
import com.twitterclone.backend.model.entities.Hashtag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HashtagService {
    List<Hashtag> linkHashtagsToTweet(String content);
    List<TrendingHashtag> getTopTrendingHashtags();
    Page<TrendingHashtag> getPaginatedTrending(Pageable pageable);
}
