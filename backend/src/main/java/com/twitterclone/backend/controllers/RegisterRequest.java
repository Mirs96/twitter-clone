package com.twitterclone.backend.controllers;

import com.twitterclone.backend.model.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String nickname;
    private LocalDate dob;
    private String sex;
    private String email;
    private String password;
    private String phone;
    @Builder.Default
    private Role role = Role.USER;
    private String profilePicture;
    private String bio;
    @Builder.Default
    private LocalDate creationDate = LocalDate.now();
}

