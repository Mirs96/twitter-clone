package com.twitterclone.backend.model.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "hashtags")
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private long id;


}
