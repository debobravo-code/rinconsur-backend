package com.rinconsur.rinconsur_backend.controller;

import com.rinconsur.rinconsur_backend.dto.LoginRequest;
import com.rinconsur.rinconsur_backend.exception.UnauthorizedException;
import com.rinconsur.rinconsur_backend.model.User;
import com.rinconsur.rinconsur_backend.repository.UserRepository;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(
            UserRepository userRepository) {

        this.userRepository = userRepository;
        
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(
            @Valid @RequestBody LoginRequest request) {

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                    new UnauthorizedException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(
                request.password(), user.getPassword())) {
            throw new UnauthorizedException("Credenciales incorrectas");
        }

        return ResponseEntity.ok(user);
    }
}