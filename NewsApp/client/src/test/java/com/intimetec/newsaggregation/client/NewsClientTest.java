package com.intimetec.newsaggregation.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.http.HttpRequest;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static com.intimetec.newsaggregation.testutils.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class NewsClientTest {

    private NewsClient newsClient;
    private ObjectMapper objectMapper;

    @Mock
    private AuthClient mockAuthClient;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        newsClient = new NewsClient(objectMapper, mockAuthClient);
    }

    @Test
    void testNewsClientCreation() {
        NewsClient client = new NewsClient(objectMapper, mockAuthClient);
        assertNotNull(client);
    }

    @Test
    void testBuildDateRangeUrl_ValidDates() {
        LocalDate from = LocalDate.parse(TEST_DATE_2024_01_01);
        LocalDate to = LocalDate.parse(TEST_DATE_2024_01_31);

        String url = buildDateRangeUrl(newsClient, from, to);

        assertNotNull(url);
        assertTrue(url.contains("from=" + TEST_DATE_2024_01_01));
        assertTrue(url.contains("to=" + TEST_DATE_2024_01_31));
        assertTrue(url.startsWith(NEWS_BASE_URL));
    }

    @Test
    void testBuildDateCategoryUrl_ValidInput() {
        LocalDate date = LocalDate.parse(TEST_DATE_2024_01_15);

        String url = buildDateCategoryUrl(newsClient, date, TEST_CATEGORY);

        assertNotNull(url);
        assertTrue(url.contains("date=" + TEST_DATE_2024_01_15));
        assertTrue(url.contains("category=" + TEST_CATEGORY));
        assertTrue(url.startsWith(NEWS_BASE_URL));
    }

    @Test
    void testBuildSearchUrl_SimpleQuery() {
        String url = buildSearchUrl(newsClient, TEST_QUERY, null, null);

        assertNotNull(url);
        assertTrue(url.contains("query=" + TEST_QUERY));
        assertTrue(url.startsWith(NEWS_BASE_URL + "/search"));
    }

    @Test
    void testBuildSearchUrl_WithDateRange() {
        LocalDate from = LocalDate.parse(TEST_DATE_2024_01_01);
        LocalDate to = LocalDate.parse(TEST_DATE_2024_01_31);

        String url = buildSearchUrl(newsClient, TEST_QUERY, from, to);

        assertNotNull(url);
        assertTrue(url.contains("query=" + TEST_QUERY));
        assertTrue(url.contains("from=" + TEST_DATE_2024_01_01));
        assertTrue(url.contains("to=" + TEST_DATE_2024_01_31));
        assertTrue(url.startsWith(NEWS_BASE_URL + "/search"));
    }

    @Test
    void testCreateGetRequest_ValidUrl() {
        when(mockAuthClient.getJwtToken()).thenReturn(TEST_JWT_TOKEN);

        HttpRequest request = createGetRequest(newsClient, NEWS_BASE_URL + "/today");

        assertNotNull(request);
        assertEquals(GET_METHOD, request.method());
        assertTrue(request.headers().firstValue(AUTHORIZATION_HEADER).isPresent());
        assertTrue(request.headers().firstValue(ACCEPT_HEADER).isPresent());
        assertEquals(BEARER_PREFIX + TEST_JWT_TOKEN, request.headers().firstValue(AUTHORIZATION_HEADER).get());
        assertEquals(APPLICATION_JSON, request.headers().firstValue(ACCEPT_HEADER).get());
    }

    // Helper methods to access private methods for testing
    private String buildDateRangeUrl(NewsClient client, LocalDate from, LocalDate to) {
        try {
            java.lang.reflect.Method method = NewsClient.class.getDeclaredMethod("buildDateRangeUrl", LocalDate.class, LocalDate.class);
            method.setAccessible(true);
            return (String) method.invoke(client, from, to);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private String buildDateCategoryUrl(NewsClient client, LocalDate date, String category) {
        try {
            java.lang.reflect.Method method = NewsClient.class.getDeclaredMethod("buildDateCategoryUrl", LocalDate.class, String.class);
            method.setAccessible(true);
            return (String) method.invoke(client, date, category);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private String buildSearchUrl(NewsClient client, String query, LocalDate from, LocalDate to) {
        try {
            java.lang.reflect.Method method = NewsClient.class.getDeclaredMethod("buildSearchUrl", String.class, LocalDate.class, LocalDate.class);
            method.setAccessible(true);
            return (String) method.invoke(client, query, from, to);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private HttpRequest createGetRequest(NewsClient client, String url) {
        try {
            java.lang.reflect.Method method = NewsClient.class.getDeclaredMethod("createGetRequest", String.class);
            method.setAccessible(true);
            return (HttpRequest) method.invoke(client, url);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
} 