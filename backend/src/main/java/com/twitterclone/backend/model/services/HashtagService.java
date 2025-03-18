package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.entities.Hashtag;

import java.util.List;

public interface HashtagService {
    Hashtag createHashtag(Hashtag hashtag);
    List<Hashtag> createHashtagsFromTweet(String content);
}
