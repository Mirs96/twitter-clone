package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.entities.Hashtag;

import java.util.List;

public interface HashtagService {
    public Hashtag createHashtag(Hashtag hashtag);
    public List<Hashtag> createHashtagsFromTweet(String content);
}
