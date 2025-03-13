package com.twitterclone.backend.model.exceptions;

public class ReactionAlreadyExistsException extends Exception {
    private String entityName;

    public ReactionAlreadyExistsException(String message, String entityName) {
        super(message);
        this.entityName = entityName;
    }

    public String getFullMessage(){
        return String.format("%s %s", getMessage(), entityName);
    }}
