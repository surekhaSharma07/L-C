package com.intimetec.newsaggreation.controller;

import com.intimetec.newsaggreation.dto.AuthRequest;
import com.intimetec.newsaggreation.dto.AuthResponse;
import com.intimetec.newsaggreation.model.User;
import com.intimetec.newsaggreation.service.UserService;
import com.intimetec.newsaggreation.config.JwtUtil;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, BCryptPasswordEncoder encoder) {
        this.userService = userService;
        this.authManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.encoder = encoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody AuthRequest req) {
        User user = userService.register(req.getEmail(), req.getPassword());

        /* 1️⃣  Get the role name here */
        String roleName = user.getRole().getName();   // "ADMIN" or "USER"

        /* 2️⃣  Generate a token that also carries the role (optional but useful) */
        String token = jwtUtil.generateToken(user.getEmail(), roleName);  // ← use the 2‑arg overload

        /* 3️⃣  Return it in AuthResponse so the client can read it */
        return ResponseEntity.ok(
                new AuthResponse(user.getId(), user.getEmail(), token, roleName)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        User user = userService.findByEmail(req.getEmail());
        if (!encoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credential");
        }

        /* 1️⃣  Same role extraction */
        String roleName = user.getRole().getName();

        /* 2️⃣  Token with role (optional) */
        String token = jwtUtil.generateToken(user.getEmail(), roleName);

        /* 3️⃣  Include role in the response */
        return ResponseEntity.ok(
                new AuthResponse(user.getId(), user.getEmail(), token, roleName)
        );
    }


}

