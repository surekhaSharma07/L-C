package com.intimetec.newsaggreation.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    @Test
    void testConstructor() {
        assertNotNull(jwtAuthenticationFilter);
    }

    @Test
    void testDoFilterInternal_WithNoAuthorizationHeader() throws Exception {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader(HttpHeaders.AUTHORIZATION);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithNonBearerToken() throws Exception {
        String authHeader = "Basic dXNlcjpwYXNz";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(authHeader);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader(HttpHeaders.AUTHORIZATION);
        verify(filterChain).doFilter(request, response);
    }
} 