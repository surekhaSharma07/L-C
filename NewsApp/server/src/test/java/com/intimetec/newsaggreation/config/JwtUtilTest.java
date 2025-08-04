package com.intimetec.newsaggreation.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "testSecretKeyForJwtTokenGenerationAndValidation123456789");
    }

    @Test
    void testGenerateToken_WithUsernameAndRole() {
        String token = jwtUtil.generateToken("testuser", "ADMIN");

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }


    @Test
    void testExtractUsername_WithRoleToken() {
        String username = "adminuser";
        String token = jwtUtil.generateToken(username, "ADMIN");

        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateToken_WithRoleToken() {
        String token = jwtUtil.generateToken("testuser", "USER");

        boolean isValid = jwtUtil.validateToken(token);
        assertTrue(isValid);
    }
} 