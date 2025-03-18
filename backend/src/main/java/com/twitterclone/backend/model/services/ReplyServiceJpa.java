package com.twitterclone.backend.model.services;

import com.twitterclone.backend.model.DisplayTweet;
import com.twitterclone.backend.model.entities.Reply;
import com.twitterclone.backend.model.entities.Tweet;
import com.twitterclone.backend.model.entities.User;
import com.twitterclone.backend.model.exceptions.EntityNotFoundException;
import com.twitterclone.backend.model.exceptions.UnauthorizedException;
import com.twitterclone.backend.model.repositories.ReplyRepositoryJpa;
import com.twitterclone.backend.model.repositories.TweetRepositoryJpa;
import com.twitterclone.backend.model.repositories.UserRepositoryJpa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReplyServiceJpa implements ReplyService {

    private UserRepositoryJpa userRepo;
    private ReplyRepositoryJpa replyRepo;
    private TweetRepositoryJpa tweetRepo;

    @Autowired
    public ReplyServiceJpa(UserRepositoryJpa userRepo, ReplyRepositoryJpa replyRepo, TweetRepositoryJpa tweetRepo) {
        this.userRepo = userRepo;
        this.replyRepo = replyRepo;
        this.tweetRepo = tweetRepo;
    }

    @Override
    public Reply createReplyToTweet(Reply reply, long userId, long tweetId, Long parentReplyId) throws EntityNotFoundException {
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
        return replyRepo.save(reply);
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
    public Page<Reply> getReplyByTweetId(long tweetId, Pageable pageable) {
        return replyRepo.getReplyByTweetId(tweetId, pageable);
    }
}
