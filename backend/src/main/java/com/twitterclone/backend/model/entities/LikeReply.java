package com.twitterclone.backend.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Likes_Replies")
public class LikeReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "reply_id", nullable = false)
    private Reply reply;

    @Column(name = "emoji", nullable = false)
    private String emoji;

    public LikeReply() {
    }

    public LikeReply(long id, User user, Reply reply, String emoji) {
        this.id = id;
        this.user = user;
        this.reply = reply;
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

    public Reply getReply() {
        return reply;
    }

    public void setReply(Reply reply) {
        this.reply = reply;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}
