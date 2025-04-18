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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReplyServiceJpa implements ReplyService {

    private UserRepositoryJpa userRepo;
    private ReplyRepositoryJpa replyRepo;
    private TweetRepositoryJpa tweetRepo;
    private LikeReplyRepositoryJpa likeReplyRepo;

    @Autowired
    public ReplyServiceJpa(UserRepositoryJpa userRepo, ReplyRepositoryJpa replyRepo, TweetRepositoryJpa tweetRepo, LikeReplyRepositoryJpa likeReplyRepo) {
        this.userRepo = userRepo;
        this.replyRepo = replyRepo;
        this.tweetRepo = tweetRepo;
        this.likeReplyRepo = likeReplyRepo;
    }

    @Override
    public DisplayReply createReplyToTweet(Reply reply, long userId, long tweetId, Long parentReplyId) throws EntityNotFoundException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));
        Tweet tweet = tweetRepo.findById(tweetId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", Tweet.class.getName()));
        Reply parentReply = null;
        if (parentReplyId != null) {
            parentReply = replyRepo.findById(parentReplyId)
                    .orElseThrow(() -> new EntityNotFoundException("Entity not found", Reply.class.getName()));
        }

        reply.setUser(user);
        reply.setTweet(tweet);
        reply.setParentReply(parentReply);
        reply = replyRepo.save(reply);

        DisplayReply replyDetails = new DisplayReply(reply);
        updateReplyDetails(replyDetails, userId);

        return replyDetails;
    }

    @Override
    public DisplayReply findReplyById(long replyId, long userId) throws EntityNotFoundException {
        DisplayReply reply= replyRepo.findById(replyId)
                .map(DisplayReply::new)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", Reply.class.getName()));

        updateReplyDetails(reply, userId);

        return reply;
    }

    @Override
    public Page<DisplayReply> getRepliesByUserId(long userId, Pageable pageable) throws EntityNotFoundException {
        userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));

        Page<DisplayReply> replies = replyRepo.getRepliesByUserId(userId, pageable)
                .map(DisplayReply::new);

        replies.forEach(reply -> updateReplyDetails(reply, userId));

        return replies;
    }

    @Override
    public void deleteReplyFromTweet(long id, long userId) throws EntityNotFoundException, UnauthorizedException {
        Reply reply = replyRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", Reply.class.getName()));

        if (reply.getUser().getId() != userId) {
            throw new UnauthorizedException("User is not authorized to delete this", Reply.class.getName());
        }

        replyRepo.deleteById(reply.getId());
    }

    @Override
    public Page<DisplayReply> getMainRepliesByTweetId(long tweetId, long userId, Pageable pageable) throws EntityNotFoundException {
        tweetRepo.findById(tweetId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", Tweet.class.getName()));
        userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));

        Page<DisplayReply> replies = replyRepo.findMainRepliesByTweet(tweetId, pageable)
                .map(DisplayReply::new);

        replies.forEach(r -> updateReplyDetails(r, userId));

        return replies;
    }

    @Override
    public List<DisplayReply> getNestedRepliesByParentReplyId(long parentReplyId, long userId) throws EntityNotFoundException{
        replyRepo.findById(parentReplyId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", Reply.class.getName()));
        userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));

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
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", User.class.getName()));
        Reply reply = replyRepo.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", Reply.class.getName()));

        if (likeReplyRepo.findLikeByUserIdAndReplyId(userId, replyId).isPresent()) {
            throw new ReactionAlreadyExistsException("Reaction already present", LikeReply.class.getName());
        }

        like.setUser(user);
        like.setReply(reply);

        likeReplyRepo.save(like);
        System.out.println(like);

        DisplayReply replyDetails = new DisplayReply(reply);
        updateReplyDetails(replyDetails, userId);

        return replyDetails;
    }

    @Override
    public DisplayReply deleteLikeFromReply(long id, long userId) throws EntityNotFoundException, UnauthorizedException {
        LikeReply like = likeReplyRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found", LikeReply.class.getName()));

        if (like.getUser().getId() != userId) {
            throw new UnauthorizedException("User is not authorized to delete this", LikeReply.class.getName());
        }

        DisplayReply reply = new DisplayReply(like.getReply());

        likeReplyRepo.deleteById(like.getId());

        // Update tweet details after like deletion
        updateReplyDetails(reply, userId);

        return reply;
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
