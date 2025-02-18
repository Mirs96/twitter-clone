package com.twitterclone.backend.controllers;

import com.twitterclone.backend.model.entities.Role;

import java.time.LocalDate;

public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String nickname;
    private LocalDate dob;
    private String sex;
    private String email;
    private String password;
    private String phone;
    private Role role;
    private String profilePicture;
    private String bio;
    private LocalDate creationDate;

    public RegisterRequest(String firstname, String lastname, String nickname, LocalDate dob, String sex, String email, String password, String phone, Role role, String profilePicture, String bio, LocalDate creationDate) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.nickname = nickname;
        this.dob = dob;
        this.sex = sex;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.profilePicture = profilePicture;
        this.bio = bio;
        this.creationDate = creationDate;
    }

    public RegisterRequest(){}

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }
}

