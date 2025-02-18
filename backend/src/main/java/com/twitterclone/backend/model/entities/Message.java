package com.twitterclone.backend.model.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "Messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
        private long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    private String content;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "creation_time")
    private LocalTime creationTime;

    @ManyToOne(optional = true)
    @JoinColumn(name = "parent_message_id")
    private Message parentMessage;

    public Message() {
    }

    public Message(long id, User sender, User receiver, String content, LocalDate creationDate, LocalTime creationTime, Message parentMessage) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
        this.parentMessage = parentMessage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
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

    public Message getParentMessage() {
        return parentMessage;
    }

    public void setParentMessage(Message parentMessage) {
        this.parentMessage = parentMessage;
    }
}

