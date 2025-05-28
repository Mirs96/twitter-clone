package com.twitterclone.backend.controllers;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for user authentication (login)")
public class AuthenticationRequest {

    @Schema(description = "User's email address for login", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "User's password for login", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}