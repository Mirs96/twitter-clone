package com.twitterclone.backend.controllers;

import com.twitterclone.backend.model.entities.Role;

import java.time.LocalDate;

public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String nickname;
    private LocalDate dob;
    private String sex;
    private String mail;
    private String pass;
    private String phone;
    private Role role;


    public RegisterRequest(String firstname, String lastname, String nickname, LocalDate dob, String sex, String mail, String pass, String phone, Role role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.nickname = nickname;
        this.dob = dob;
        this.sex = sex;
        this.mail = mail;
        this.pass = pass;
        this.phone = phone;
        this.role = role;
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
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
}

