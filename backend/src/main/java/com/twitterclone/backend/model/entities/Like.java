package com.twitterclone.backend.model.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private long id;

}

