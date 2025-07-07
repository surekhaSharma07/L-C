package com.intimetec.newsaggregation.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static com.intimetec.newsaggregation.testutils.TestConstants.*;

class AdminClientTest {
    @Test
    void testAdminClientCreation() {
        AdminClient client = new AdminClient(BASE_URL, TEST_JWT_TOKEN);
        assertNotNull(client);
    }

    @Test
    void testApiSourceDtoCreation() {
        AdminClient.ApiSourceDto dto = new AdminClient.ApiSourceDto(1, TEST_NAME, TEST_URL, TEST_API_KEY, TEST_POLLING_FREQ, TEST_STATUS);
        assertEquals(1, dto.getId());
        assertEquals(TEST_NAME, dto.getName());
        assertEquals(TEST_URL, dto.getEndpointUrl());
        assertEquals(TEST_API_KEY, dto.getApiKey());
        assertEquals(TEST_POLLING_FREQ, dto.getPollingFreq());
        assertEquals(TEST_STATUS, dto.getStatus());
    }

    @Test
    void testReportDtoCreation() {
        AdminClient.ReportDto dto = new AdminClient.ReportDto(TEST_EMAIL, TEST_REPORTED_AT, TEST_REASON);
        assertEquals(TEST_EMAIL, dto.getUserEmail());
        assertEquals(TEST_REPORTED_AT, dto.getReportedAt());
        assertEquals(TEST_REASON, dto.getReason());
    }
} 