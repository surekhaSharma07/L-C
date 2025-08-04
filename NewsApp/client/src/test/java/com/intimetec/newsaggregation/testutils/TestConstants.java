package com.intimetec.newsaggregation.testutils;

public final class TestConstants {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ACCEPT_HEADER = "Accept";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String APPLICATION_JSON = "application/json";

    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";
    public static final String PUT_METHOD = "PUT";
    public static final String DELETE_METHOD = "DELETE";

    public static final String TEST_JWT_TOKEN = "test-token";
    public static final String TEST_EMAIL = "test@example.com";
    public static final String TEST_PASSWORD = "password123";
    public static final String TEST_REASON = "Test reason";
    

    public static final long TEST_ARTICLE_ID = 1L;
    public static final long TEST_ARTICLE_ID_2 = 2L;

    public static final String BASE_URL = "http://localhost:8081";
    public static final String API_BASE_URL = BASE_URL + "/api";
    public static final String NEWS_BASE_URL = API_BASE_URL + "/news";
    public static final String USER_ARTICLES_BASE_URL = API_BASE_URL + "/user/articles";
    public static final String NOTIFY_PREFS_URL = API_BASE_URL + "/notify/prefs";
    public static final String NOTIFY_LIST_URL = API_BASE_URL + "/notify";

    public static final String TEST_ARTICLE_JSON = "{\"id\":1,\"title\":\"Test Article\"}";
    public static final String TEST_ARTICLES_ARRAY = "[{\"id\":1,\"title\":\"Article 1\"},{\"id\":2,\"title\":\"Article 2\"}]";
    public static final String TEST_NOTIFICATIONS_ARRAY = "[{\"id\":1,\"message\":\"Test notification\"},{\"id\":2,\"message\":\"Another notification\"}]";
    public static final String TEST_CONFIG_JSON = "{\"email\":true,\"sms\":false,\"push\":true}";
    public static final String EMPTY_ARRAY_JSON = "[]";
    public static final String SUCCESS_MESSAGE = "Success message";

    public static final String TEST_QUERY = "technology";
    public static final String TEST_CATEGORY = "technology";
    public static final String TEST_NAME = "name";
    public static final String TEST_URL = "url";
    public static final String TEST_API_KEY = "key";
    public static final String TEST_STATUS = "ACTIVE";
    public static final int TEST_POLLING_FREQ = 10;

    public static final String TEST_DATE_2024_01_01 = "2024-01-01";
    public static final String TEST_DATE_2024_01_15 = "2024-01-15";
    public static final String TEST_DATE_2024_01_31 = "2024-01-31";
    public static final String TEST_REPORTED_AT = "2024-07-07T10:00:00";

    public static final String EXPECTED_NOT_NULL = "Expected not null";
    public static final String EXPECTED_EMPTY = "Expected empty";
    public static final String EXPECTED_SIZE = "Expected size";
    
    private TestConstants() {
    }
} 