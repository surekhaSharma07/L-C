package com.intimetec.newsaggreation.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
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
        // Set the secret key for testing
        ReflectionTestUtils.setField(jwtUtil, "secret", "testSecretKeyForJwtTokenGenerationAndValidation123456789");
    }

    @Test
    void testGenerateToken_WithUsername() {
        // Act
        String token = jwtUtil.generateToken("testuser");

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void testGenerateToken_WithUsernameAndRole() {
        // Act
        String token = jwtUtil.generateToken("testuser", "ADMIN");

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void testGenerateToken_WithEmptyUsername() {
        // Act
        String token = jwtUtil.generateToken("");

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void testExtractUsername_ValidToken() {
        // Arrange
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    void testExtractUsername_WithRoleToken() {
        // Arrange
        String username = "adminuser";
        String token = jwtUtil.generateToken(username, "ADMIN");

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    void testValidateToken_ValidToken() {
        // Arrange
        String token = jwtUtil.generateToken("testuser");

        // Act
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_WithRoleToken() {
        // Arrange
        String token = jwtUtil.generateToken("testuser", "USER");

        // Act
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.jwt.token";

        // Act
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_EmptyToken() {
        // Arrange
        String emptyToken = "";

        // Act
        boolean isValid = jwtUtil.validateToken(emptyToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_NullToken() {
        // Arrange
        String nullToken = null;

        // Act
        boolean isValid = jwtUtil.validateToken(nullToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_MalformedToken() {
        // Arrange
        String malformedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.signature";

        // Act
        boolean isValid = jwtUtil.validateToken(malformedToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testExtractUsername_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.jwt.token";

        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwtUtil.extractUsername(invalidToken);
        });
    }

    @Test
    void testExtractUsername_EmptyToken() {
        // Arrange
        String emptyToken = "";

        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwtUtil.extractUsername(emptyToken);
        });
    }

    @Test
    void testExtractUsername_NullToken() {
        // Arrange
        String nullToken = null;

        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwtUtil.extractUsername(nullToken);
        });
    }

    @Test
    void testTokenExpiration() {
        // Arrange
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertEquals(username, extractedUsername);
        assertTrue(isValid);
    }

    @Test
    void testMultipleTokenGeneration() {
        // Arrange
        String username1 = "user1";
        String username2 = "user2";

        // Act
        String token1 = jwtUtil.generateToken(username1);
        String token2 = jwtUtil.generateToken(username2);

        // Assert
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2);
        assertEquals(username1, jwtUtil.extractUsername(token1));
        assertEquals(username2, jwtUtil.extractUsername(token2));
    }

    @Test
    void testTokenWithSpecialCharacters() {
        // Arrange
        String username = "user@example.com";

        // Act
        String token = jwtUtil.generateToken(username);
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void testTokenWithLongUsername() {
        // Arrange
        String username = "verylongusernamewithlotsofcharacters123456789";

        // Act
        String token = jwtUtil.generateToken(username);
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername);
        assertTrue(jwtUtil.validateToken(token));
    }
} 