package com.intimetec.newsaggregation.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Handles authentication operations (login and signup) with the server.
 */
public class AuthClient {
    private static final String BASE_URL = "http://localhost:8081/api/auth";
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private String jwtToken;
    private String role;

    public AuthClient(ObjectMapper mapper) {
        this.mapper = mapper;
        this.httpClient = createHttpClient();
    }

    /**
     * Creates and configures the HTTP client.
     */
    private HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .build();
    }

    /**
     * Signs up a new user.
     * 
     * @param email user's email
     * @param password user's password
     * @return true if signup successful, false otherwise
     * @throws Exception if network or parsing error occurs
     */
    public boolean signup(String email, String password) throws Exception {
        String requestBody = createAuthRequestBody(email, password);
        HttpRequest request = createPostRequest(BASE_URL + "/signup", requestBody);
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    /**
     * Logs in an existing user.
     * 
     * @param email user's email
     * @param password user's password
     * @return true if login successful, false otherwise
     * @throws Exception if network or parsing error occurs
     */
    public boolean login(String email, String password) throws Exception {
        String requestBody = createAuthRequestBody(email, password);
        HttpRequest request = createPostRequest(BASE_URL + "/login", requestBody);
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return processLoginResponse(response);
    }

    /**
     * Creates the authentication request body JSON.
     */
    private String createAuthRequestBody(String email, String password) throws Exception {
        JsonNode requestNode = mapper.createObjectNode()
                .put("email", email)
                .put("password", password);
        return mapper.writeValueAsString(requestNode);
    }

    /**
     * Creates a POST request with JSON content.
     */
    private HttpRequest createPostRequest(String url, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    /**
     * Processes the login response and extracts JWT token and role.
     */
    private boolean processLoginResponse(HttpResponse<String> response) throws Exception {
        if (response.statusCode() == 200) {
            JsonNode root = mapper.readTree(response.body());
            this.jwtToken = root.path("token").asText(null);
            this.role = root.path("role").asText(null);
            return jwtToken != null;
        }
        return false;
    }

    /**
     * Gets the current JWT token.
     * 
     * @return the JWT token or null if not logged in
     */
    public String getJwtToken() {
        return jwtToken;
    }

    /**
     * Gets the current user's role.
     * 
     * @return the user's role or null if not logged in
     */
    public String getRole() {
        return role;
    }
}
