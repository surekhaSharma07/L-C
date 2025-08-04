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
        BCryptPasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }

    @Test
    void testPasswordEncoderBeanIsNotNull() {
        BCryptPasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        assertNotNull(passwordEncoder, "BCryptPasswordEncoder should not be null");
    }

    @Test
    void testPasswordEncoderBeanIsNewInstance() {
        BCryptPasswordEncoder passwordEncoder1 = securityConfig.passwordEncoder();
        BCryptPasswordEncoder passwordEncoder2 = securityConfig.passwordEncoder();

        assertNotNull(passwordEncoder1);
        assertNotNull(passwordEncoder2);
        assertNotSame(passwordEncoder1, passwordEncoder2);
    }

    @Test
    void testPasswordEncoderCanEncodeAndMatch() {
        BCryptPasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        assertFalse(passwordEncoder.matches("wrongPassword", encodedPassword));
    }

    @Test
    void testAuthenticationManagerBean() throws Exception {
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        assertNotNull(result);
        assertEquals(authenticationManager, result);
        verify(authenticationConfiguration).getAuthenticationManager();
    }

    @Test
    void testAuthenticationManagerBeanIsNotNull() throws Exception {
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        AuthenticationManager result = securityConfig.authenticationManager(authenticationConfiguration);

        assertNotNull(result, "AuthenticationManager should not be null");
    }

    @Test
    void testSecurityConfigConstructor() {
        assertNotNull(securityConfig);
        assertDoesNotThrow(() -> {
            SecurityConfig config = new SecurityConfig(jwtFilter);
            assertNotNull(config);
        });
    }

    @Test
    void testSecurityConfigWithNullJwtFilter() {
        assertDoesNotThrow(() -> {
            SecurityConfig config = new SecurityConfig(null);
            assertNotNull(config);
        });
    }
} 