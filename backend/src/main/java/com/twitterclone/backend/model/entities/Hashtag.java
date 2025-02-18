package com.twitterclone.backend.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Hashtags")
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private long id;

    private String keyword;

    public Hashtag() {
    }

    public Hashtag(long id, String keyword) {
        this.id = id;
        this.keyword = keyword;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
