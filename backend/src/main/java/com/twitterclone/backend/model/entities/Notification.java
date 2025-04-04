package com.twitterclone.backend.model.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "Notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String content;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "creation_time")
    private LocalTime creationTime;

    public Notification() {
    }

    public Notification(long id, User user, String content, LocalDate creationDate, LocalTime creationTime) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalTime creationTime) {
        this.creationTime = creationTime;
    }
}
