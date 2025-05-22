package com.twitterclone.backend.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Tweets")
public class Tweet extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String content;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "Tweets_Hashtags",
            joinColumns = @JoinColumn(name = "tweet_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Hashtag> hashtags =  new ArrayList<>();

    public void addHashtag(Hashtag hashtag) {
        this.hashtags.add(hashtag);
        if (hashtag.getTweets() == null) {
            hashtag.setTweets(new ArrayList<>());
        }
        hashtag.getTweets().add(this);
    }

    public void removeHashtag(Hashtag hashtag) {
        this.hashtags.remove(hashtag);
        if (hashtag.getTweets() != null) {
            hashtag.getTweets().remove(this);
        }
    }
}
