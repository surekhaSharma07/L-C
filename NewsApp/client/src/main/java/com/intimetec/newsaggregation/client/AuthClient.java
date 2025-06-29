package com.intimetec.newsaggregation.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.*;

public class AuthClient {
    private static final String BASE = "http://localhost:8081/api/auth";
    private final HttpClient http;
    private final ObjectMapper mapper;
    private String jwtToken;
    private String role;          // ← NEW

    public AuthClient(ObjectMapper mapper) {
        this.mapper = mapper;
        this.http = HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .build();
    }

    public boolean signup(String email, String password) throws Exception {
        String json = mapper.writeValueAsString(
                mapper.createObjectNode()
                        .put("email", email)
                        .put("password", password)
        );
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/signup"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        var resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.statusCode() == 200;
    }

    public boolean login(String email, String password) throws Exception {
        String json = mapper.writeValueAsString(
                mapper.createObjectNode()
                        .put("email", email)
                        .put("password", password)
        );
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() == 200) {
            JsonNode root = mapper.readTree(resp.body());
            this.jwtToken = root.path("token").asText(null);
            this.role = root.path("role").asText(null);  // ← grab role from server JSON
            return jwtToken != null;
        }
        return false;
    }



    public String getJwtToken() {
        return jwtToken;
    }

    public String getRole() {
        return role;
    }


}
