package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.DisplayReply;
import com.twitterclone.backend.model.entities.*;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.exceptions.ReactionAlreadyExistsException;
import com.twitterclone.backend.model.exceptions.UnauthorizedException;
import com.twitterclone.backend.model.repositories.LikeReplyRepositoryJpa;
import com.twitterclone.backend.model.repositories.ReplyRepositoryJpa;
import com.twitterclone.backend.model.repositories.TweetRepositoryJpa;
import com.twitterclone.backend.model.repositories.UserRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyServiceJpa implements ReplyService {

    private final UserRepositoryJpa userRepo;
    private final ReplyRepositoryJpa replyRepo;
    private final TweetRepositoryJpa tweetRepo;
    private final LikeReplyRepositoryJpa likeReplyRepo;

    @Override
    public DisplayReply createReplyToTweet(Reply reply, long userId, long tweetId, Long parentReplyId) throws EntityNotFoundException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found", User.class.getName()));
        Tweet tweet = tweetRepo.findById(tweetId)
                .orElseThrow(() -> new EntityNotFoundException("Tweet not found", Tweet.class.getName()));
        Reply parentReplyEntity = null;
        if (parentReplyId != null) {
            parentReplyEntity = replyRepo.findById(parentReplyId)
                    .orElseThrow(() -> new EntityNotFoundException("Parent reply not found", Reply.class.getName()));
        }

        reply.setUser(user);
        reply.setTweet(tweet);
        reply.setParentReply(parentReplyEntity);
        Reply savedReply = replyRepo.save(reply);

        DisplayReply replyDetails = new DisplayReply(savedReply);
        updateReplyDetails(replyDetails, userId);

        return replyDetails;
    }

    @Override
    @Transactional(readOnly = true)
    public DisplayReply findReplyById(long replyId, long userId) throws EntityNotFoundException {
        Reply replyEntity = replyRepo.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("Reply not found", Reply.class.getName()));
        DisplayReply reply = new DisplayReply(replyEntity);
        updateReplyDetails(reply, userId);
        return reply;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DisplayReply> getRepliesByUserId(long userId, long currentUserId, Pageable pageable) throws EntityNotFoundException {
        userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found", User.class.getName()));

        Page<DisplayReply> replies = replyRepo.getRepliesByUserId(userId, pageable)
                .map(DisplayReply::new);

        replies.forEach(reply -> updateReplyDetails(reply, currentUserId));

        return replies;
    }

    @Override
    public void deleteReplyFromTweet(long id, long userId) throws EntityNotFoundException, UnauthorizedException {
        Reply reply = replyRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reply not found", Reply.class.getName()));

        if (reply.getUser().getId() != userId) {
            throw new UnauthorizedException("User is not authorized to delete this reply", Reply.class.getName());
        }

        replyRepo.deleteById(reply.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DisplayReply> getMainRepliesByTweetId(long tweetId, long userId, Pageable pageable) throws EntityNotFoundException {
        tweetRepo.findById(tweetId)
                .orElseThrow(() -> new EntityNotFoundException("Tweet not found", Tweet.class.getName()));
        userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found", User.class.getName()));

        Page<DisplayReply> replies = replyRepo.findMainRepliesByTweet(tweetId, pageable)
                .map(DisplayReply::new);

        replies.forEach(r -> updateReplyDetails(r, userId));

        return replies;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisplayReply> getNestedRepliesByParentReplyId(long parentReplyId, long userId) throws EntityNotFoundException{
        replyRepo.findById(parentReplyId)
                .orElseThrow(() -> new EntityNotFoundException("Parent reply not found", Reply.class.getName()));
        userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found", User.class.getName()));

        List<DisplayReply> nested = replyRepo.findNestedRepliesByParentReplyId(parentReplyId)
                .stream()
                .map(DisplayReply::new)
                .toList();

        nested.forEach(n -> updateReplyDetails(n, userId));

        return nested;
    }

    @Override
    public DisplayReply createLikeToReply(LikeReply like, long userId, long replyId) throws EntityNotFoundException, ReactionAlreadyExistsException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found", User.class.getName()));
        Reply replyEntity = replyRepo.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("Reply not found", Reply.class.getName()));

        if (likeReplyRepo.findLikeByUserIdAndReplyId(userId, replyId).isPresent()) {
            throw new ReactionAlreadyExistsException("Reaction already present for this reply", LikeReply.class.getName());
        }

        like.setUser(user);
        like.setReply(replyEntity);

        likeReplyRepo.save(like);

        DisplayReply replyDetails = new DisplayReply(replyEntity);
        updateReplyDetails(replyDetails, userId);

        return replyDetails;
    }

    @Override
    public DisplayReply deleteLikeFromReply(long id, long userId) throws EntityNotFoundException, UnauthorizedException {
        LikeReply like = likeReplyRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Like not found for this reply", LikeReply.class.getName()));

        if (like.getUser().getId() != userId) {
            throw new UnauthorizedException("User is not authorized to delete this like", LikeReply.class.getName());
        }

        Reply likedReply = like.getReply();
        likeReplyRepo.deleteById(like.getId());

        DisplayReply replyDetails = new DisplayReply(likedReply);
        updateReplyDetails(replyDetails, userId);

        return replyDetails;
    }

    private void updateReplyDetails(DisplayReply replyDetails, long userId) {
        long replyId = replyDetails.getReply().getId();

        replyDetails.setLikeCount(likeReplyRepo.countLikesByReplyId(replyId));
        Optional<LikeReply> oLike = likeReplyRepo.findLikeByUserIdAndReplyId(userId, replyId);
        replyDetails.setLiked(oLike.isPresent());
        oLike.ifPresent(l -> replyDetails.setLikeId(l.getId()));
        replyDetails.setHasNestedReplies(replyRepo.hasNestedReplies(replyId));
    }
}
