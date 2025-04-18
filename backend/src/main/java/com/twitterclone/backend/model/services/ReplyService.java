package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.DisplayReply;
import com.twitterclone.backend.model.entities.LikeReply;
import com.twitterclone.backend.model.entities.Reply;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.exceptions.ReactionAlreadyExistsException;
import com.twitterclone.backend.model.exceptions.UnauthorizedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReplyService {

    DisplayReply createReplyToTweet(Reply reply, long userId, long tweetId, Long parentReplyId) throws EntityNotFoundException;
    void deleteReplyFromTweet(long id, long userId) throws EntityNotFoundException, UnauthorizedException;

    DisplayReply findReplyById(long replyId, long userId) throws EntityNotFoundException;
    Page<DisplayReply> getRepliesByUserId(long userId, Pageable pageable) throws EntityNotFoundException;

    Page<DisplayReply> getMainRepliesByTweetId(long tweetId, long userId, Pageable pageable) throws EntityNotFoundException;
    List<DisplayReply> getNestedRepliesByParentReplyId(long parentReplyId, long userId) throws EntityNotFoundException;

    DisplayReply createLikeToReply(LikeReply like, long userId, long replyId) throws EntityNotFoundException, ReactionAlreadyExistsException;
    DisplayReply deleteLikeFromReply(long id, long userId) throws EntityNotFoundException, UnauthorizedException;
}
