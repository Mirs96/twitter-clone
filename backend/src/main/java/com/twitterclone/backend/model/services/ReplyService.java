package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.entities.Reply;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.exceptions.UnauthorizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReplyService {
    Reply createReplyToTweet(Reply reply, long userId, long tweetId, Long parentReplyId) throws EntityNotFoundException;
    void deleteReplyFromTweet(long id, long userId) throws EntityNotFoundException, UnauthorizedException;
    Page<Reply> getReplyByTweetId(long tweetId, Pageable pageable);
}
