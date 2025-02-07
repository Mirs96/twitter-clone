package com.twitterclone.backend.model.services;

import com.twitterclone.backend.controllers.AuthenticationRequest;
import com.twitterclone.backend.controllers.AuthenticationResponse;
import com.twitterclone.backend.controllers.RegisterRequest;
import com.twitterclone.backend.model.entities.User;
import com.twitterclone.backend.model.repositories.UserRepositoryJpa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepositoryJpa repository;
    private final AuthenticationManager authenticationManager;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    public AuthService( PasswordEncoder passwordEncoder, JwtService jwtService, UserRepositoryJpa repository, AuthenticationManager authenticationManager) {

        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.repository = repository;
        this.authenticationManager = authenticationManager;
    }


    public AuthenticationResponse register(RegisterRequest request) {
        logger.info("Register method called with request: {}", request);

//        var user = User.builder()
//                .firstname(request.getFirstname())
//                .lastname(request.getLastname())
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .role(request.getRole())
//                .build();

        User user = new User(null, request.getFirstname(), request.getLastname(),
                request.getNickname(),request.getDob(),request.getSex(), request.getMail(), passwordEncoder.encode(request.getPass()),request.getPhone(),request.getRole());
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = repository.findByMail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);

    }
}

