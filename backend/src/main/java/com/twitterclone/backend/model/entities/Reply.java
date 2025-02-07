package com.twitterclone.backend.model.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "replies")
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private long id;


}
