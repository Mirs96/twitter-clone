package com.twitterclone.backend.model.services;

import com.twitterclone.backend.controllers.AuthenticationRequest;
import com.twitterclone.backend.controllers.AuthenticationResponse;
import com.twitterclone.backend.controllers.RegisterRequest;
import com.twitterclone.backend.model.entities.User;
import com.twitterclone.backend.model.repositories.UserRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepositoryJpa repository;
    private final AuthenticationManager authenticationManager;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthenticationResponse register(RegisterRequest request) {
        logger.info("Register method called with request: {}", request);

        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .nickname(request.getNickname())
                .dob(request.getDob())
                .sex(request.getSex())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
                .profilePicture(request.getProfilePicture())
                .bio(request.getBio())
                .build();

        repository.save(user);
        String jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = repository.findByEmail(request.getEmail()).orElseThrow();
        String jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }
}

