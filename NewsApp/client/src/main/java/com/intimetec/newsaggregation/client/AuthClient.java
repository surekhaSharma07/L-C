package com.intimetec.newsaggregation.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intimetec.newsaggregation.exception.AuthenticationException;
import com.intimetec.newsaggregation.exception.NetworkException;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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


    private HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
                .build();
    }

    private String createAuthRequestBody(String email, String password) throws Exception {
        JsonNode requestNode = mapper.createObjectNode()
                .put("email", email)
                .put("password", password);
        return mapper.writeValueAsString(requestNode);
    }

    public boolean signup(String email, String password) throws AuthenticationException, NetworkException {
        try {
            JsonNode requestNode = mapper.createObjectNode()
                    .put("email", email)
                    .put("password", password);
            String requestBody = mapper.writeValueAsString(requestNode);
            HttpRequest request = createPostRequest(BASE_URL + "/signup", requestBody);

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return true;
            } else {
                throw new AuthenticationException("Signup failed with status: " + response.statusCode());
            }
        } catch (AuthenticationException authenticationException) {
            throw authenticationException;
        } catch (Exception exception) {
            throw new NetworkException("Network error during signup", exception);
        }
    }

    public boolean login(String email, String password) throws AuthenticationException, NetworkException {
        try {
            JsonNode requestNode = mapper.createObjectNode()
                    .put("email", email)
                    .put("password", password);
            String requestBody = mapper.writeValueAsString(requestNode);
            HttpRequest request = createPostRequest(BASE_URL + "/login", requestBody);

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return processLoginResponse(response);
        } catch (AuthenticationException authenticationException) {
            throw authenticationException;
        } catch (Exception exception) {
            throw new NetworkException("Network error during login", exception);
        }
    }

    private HttpRequest createPostRequest(String url, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private boolean processLoginResponse(HttpResponse<String> response) throws AuthenticationException, Exception {
        if (response.statusCode() == 200) {
            try {
                JsonNode root = mapper.readTree(response.body());
                this.jwtToken = root.path("token").asText(null);
                this.role = root.path("role").asText(null);
                return jwtToken != null;
            } catch (Exception e) {
                throw new Exception("Failed to parse login response", e);
            }
        } else {
            throw new AuthenticationException("Login failed with status: " + response.statusCode());
        }
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public String getRole() {
        return role;
    }
}
