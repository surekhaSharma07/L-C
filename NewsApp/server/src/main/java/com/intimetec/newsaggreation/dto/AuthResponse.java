package com.intimetec.newsaggreation.dto;

public class AuthResponse {
    private Long id;
    private String email;
    private String token;
    private String role;

    public AuthResponse(Long id, String email, String token, String role) {
        this.id = id;
        this.email = email;
        this.token = token;
        this.role = role;
    }

    public AuthResponse(Long id, String email, String msg) {
        this.email = msg;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public String getRole() {
        return role;
    }
}
