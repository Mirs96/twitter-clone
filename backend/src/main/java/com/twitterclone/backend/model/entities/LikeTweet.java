package com.twitterclone.backend.model.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "Likes_tweets")
public class LikeTweet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "tweet_id", nullable = false)
    private Tweet tweet;

    @Column(name = "emoji", nullable = false)
    @Enumerated(EnumType.STRING)
    private Emoji emoji;

    public LikeTweet() {
    }

    public LikeTweet(long id, User user, Tweet tweet, Emoji emoji) {
        this.id = id;
        this.user = user;
        this.tweet = tweet;
        this.emoji = emoji;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tweet getTweet() {
        return tweet;
    }

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public void setEmoji(Emoji emoji) {
        this.emoji = emoji;
    }
}
