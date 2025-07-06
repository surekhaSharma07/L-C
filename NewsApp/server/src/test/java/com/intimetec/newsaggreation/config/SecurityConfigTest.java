package com.intimetec.newsaggreation.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtFilter;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private AuthenticationManager authenticationManager;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig(jwtFilter);
    }

    @Test
    void testPasswordEncoderBean() {
        // Act
        BCryptPasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        // Assert
        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void testPasswordEncoderBeanIsNotNull() {
        // Act
        BCryptPasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        // Assert
        assertNotNull(passwordEncoder, "BCryptPasswordEncoder should not be null");
    }

    @Test
    void testPasswordEncoderBeanIsNewInstance() {
        // Act
        BCryptPasswordEncoder passwordEncoder1 = securityConfig.passwordEncoder();
        BCryptPasswordEncoder passwordEncoder2 = securityConfig.passwordEncoder();

        // Assert
        assertNotNull(passwordEncoder1);
        assertNotNull(passwordEncoder2);
        // Each call should return a new instance (not singleton)
        assertNotSame(passwordEncoder1, passwordEncoder2);
    }

    @Test
    void testPasswordEncoderCanEncodeAndMatch() {
        // Act
        BCryptPasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Assert
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        assertFalse(passwordEncoder.matches("wrongPassword", encodedPassword));
    }

    @Test
    void testAuthenticationManagerBean() throws Exception {
        // Arrange
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        // Act
        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        // Assert
        assertNotNull(result);
        assertEquals(authenticationManager, result);
        verify(authenticationConfiguration).getAuthenticationManager();
    }

    @Test
    void testAuthenticationManagerBeanIsNotNull() throws Exception {
        // Arrange
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        // Act
        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        // Assert
        assertNotNull(result, "AuthenticationManager should not be null");
    }

    @Test
    void testSecurityConfigConstructor() {
        // Act & Assert
        assertNotNull(securityConfig);
        assertDoesNotThrow(() -> {
            SecurityConfig config = new SecurityConfig(jwtFilter);
            assertNotNull(config);
        });
    }

    @Test
    void testSecurityConfigWithNullJwtFilter() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            SecurityConfig config = new SecurityConfig(null);
            assertNotNull(config);
        });
    }
} 