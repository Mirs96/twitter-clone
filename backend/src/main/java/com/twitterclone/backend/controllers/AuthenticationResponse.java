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
@Schema(description = "Data Transfer Object for authentication response, containing the JWT")
public class AuthenticationResponse {

    @Schema(description = "JWT (JSON Web Token) for authenticating subsequent requests",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwidXNlcklkIjoiMSIsImlhdCI6MTYxNjQwNjQwMCwiZXhwIjoxNjE2NDkyODAwfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;
}