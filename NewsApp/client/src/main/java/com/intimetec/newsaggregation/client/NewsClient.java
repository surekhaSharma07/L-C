package com.intimetec.newsaggregation.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

/**
 * Handles news-related API operations (headlines, search) with the server.
 */
public class NewsClient {

    private static final String BASE_URL = "http://localhost:8081/api/news";
    private final HttpClient httpClient;
    private final ObjectMapper mapper;
    private final AuthClient authClient;

    public NewsClient(ObjectMapper mapper, AuthClient authClient) {
        this.mapper = mapper;
        this.authClient = authClient;
        this.httpClient = HttpClient.newHttpClient();
    }

    /* ====================  HEADLINES  =========================== */

    /**
     * Fetches today's headlines.
     * 
     * @return list of today's headlines
     * @throws Exception if network or parsing error occurs
     */
    public List<JsonNode> fetchToday() throws Exception {
        return getArray(BASE_URL + "/today");
    }

    /**
     * Fetches headlines for a specific date range.
     * 
     * @param from start date (inclusive)
     * @param to end date (inclusive)
     * @return list of headlines in the date range
     * @throws Exception if network or parsing error occurs
     */
    public List<JsonNode> fetchByDateRange(LocalDate from, LocalDate to) throws Exception {
        String url = buildDateRangeUrl(from, to);
        return getArray(url);
    }

    /**
     * Fetches headlines for a specific date and category.
     * 
     * @param date the date to fetch headlines for
     * @param category the category to filter by
     * @return list of headlines for the date and category
     * @throws Exception if network or parsing error occurs
     */
    public List<JsonNode> fetchByDateAndCategory(LocalDate date, String category) throws Exception {
        String url = buildDateCategoryUrl(date, category);
        return getArray(url);
    }

    /* ====================  SEARCH  ============================== */

    /**
     * Searches for articles without date filter.
     * 
     * @param query search query
     * @return list of matching articles
     * @throws Exception if network or parsing error occurs
     */
    public List<JsonNode> search(String query) throws Exception {
        return search(query, null, null);
    }

    /**
     * Searches for articles with optional date range filter.
     * 
     * @param query search query
     * @param from start date (inclusive), null for no filter
     * @param to end date (inclusive), null for no filter
     * @return list of matching articles
     * @throws Exception if network or parsing error occurs
     */
    public List<JsonNode> search(String query, LocalDate from, LocalDate to) throws Exception {
        String url = buildSearchUrl(query, from, to);
        return getArray(url);
    }

    /* ====================  URL BUILDERS  ============================= */

    /**
     * Builds URL for date range headlines.
     */
    private String buildDateRangeUrl(LocalDate from, LocalDate to) {
        return BASE_URL + "?from=" + from + "&to=" + to;
    }

    /**
     * Builds URL for date and category headlines.
     */
    private String buildDateCategoryUrl(LocalDate date, String category) {
        return BASE_URL + "?date=" + date + "&category=" + category;
    }

    /**
     * Builds URL for article search.
     */
    private String buildSearchUrl(String query, LocalDate from, LocalDate to) {
        StringBuilder url = new StringBuilder(BASE_URL)
                .append("/search?query=")
                .append(URLEncoder.encode(query, StandardCharsets.UTF_8));

        if (from != null && to != null) {
            url.append("&from=").append(from).append("&to=").append(to);
        }

        return url.toString();
    }

    /* ====================  HELPERS  ============================= */

    /**
     * Executes GET request and returns JSON array as List&lt;JsonNode&gt;.
     * 
     * @param url the URL to request
     * @return list of JSON nodes from the response
     * @throws Exception if network or parsing error occurs
     */
    private List<JsonNode> getArray(String url) throws Exception {
        HttpRequest request = createGetRequest(url);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        validateResponse(response);
        return parseJsonArray(response.body());
    }

    /**
     * Creates a GET request with authentication headers.
     */
    private HttpRequest createGetRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + authClient.getJwtToken())
                .header("Accept", "application/json")
                .GET()
                .build();
    }

    /**
     * Validates the HTTP response.
     */
    private void validateResponse(HttpResponse<String> response) {
        if (response.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP " + response.statusCode() + " → " + response.body());
        }
    }

    /**
     * Parses JSON array response into List&lt;JsonNode&gt;.
     */
    private List<JsonNode> parseJsonArray(String responseBody) throws Exception {
        JsonNode tree = mapper.readTree(responseBody);
        return mapper.convertValue(tree, new TypeReference<>() {});
    }
}
