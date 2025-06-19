package com.intimetec.newsaggreation.dto;


public class AuthResponse {
    private Long id;
    private String email;
    private String token;
    public AuthResponse(Long id, String email, String token) {
        this.id = id; this.email = email; this.token = token;
    }

    public AuthResponse(String emailAlreadyInUse) {
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getToken() { return token; }
}