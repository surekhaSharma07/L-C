package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.model.Role;
import com.intimetec.newsaggreation.model.User;
import com.intimetec.newsaggreation.repository.RoleRepository;
import com.intimetec.newsaggreation.repository.UserRepository;
import com.intimetec.newsaggreation.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private Role userRole;
    private User testUser;
    private final BCryptPasswordEncoder realEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        userRole = new Role();
        userRole.setId(1);
        userRole.setName("USER");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash(realEncoder.encode("password123"));
        testUser.setRole(userRole);
    }

    @Test
    void testRegister_Success() {
        // Arrange
        String email = "newuser@example.com";
        String rawPassword = "password123";

        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(2L);
            return user;
        });

        // Act
        User result = userService.register(email, rawPassword);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertTrue(realEncoder.matches(rawPassword, result.getPasswordHash()));
        assertEquals(userRole, result.getRole());
        assertEquals(2L, result.getId());

        verify(roleRepository).findByName("USER");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegister_WhenUserRoleNotFound() {
        // Arrange
        String email = "newuser@example.com";
        String rawPassword = "password123";

        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
            userService.register(email, rawPassword)
        );

        assertEquals("Default role USER not found", exception.getMessage());
        verify(roleRepository).findByName("USER");
        verify(userRepository, never()).save(any(User.class));
    }

//    @Test
//    void testRegister_WithNullEmail() {
//        // Arrange
//        String email = null;
//        String rawPassword = "password123";
//
//        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
//        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Act
//        User result = userService.register(email, rawPassword);
//
//        // Assert
//        assertNotNull(result);
//        assertNull(result.getEmail());
//        verify(userRepository).save(any(User.class));
//    }

//    @Test
//    void testRegister_WithEmptyPassword() {
//        // Arrange
//        String email = "test@example.com";
//        String rawPassword = "";
//
//        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
//        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Act
//        User result = userService.register(email, rawPassword);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(realEncoder.matches(rawPassword, result.getPasswordHash()));
//        verify(userRepository).save(any(User.class));
//    }

    @Test
    void testFindByEmail_Success() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // Act
        User result = userService.findByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(testUser.getId(), result.getId());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testFindByEmail_WhenUserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
            userService.findByEmail(email)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testFindByEmail_WithNullEmail() {
        // Arrange
        String email = null;
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
            userService.findByEmail(email)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testFindByEmail_WithEmptyEmail() {
        // Arrange
        String email = "";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
            userService.findByEmail(email)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void testRegister_VerifyPasswordEncryption() {
        // Arrange
        String email = "test@example.com";
        String rawPassword = "myPassword123";

        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        User result = userService.register(email, rawPassword);

        // Assert
        assertTrue(realEncoder.matches(rawPassword, result.getPasswordHash()));
        // No need to verify passwordEncoder mock
    }

    @Test
    void testRegister_VerifyUserRoleAssignment() {
        // Arrange
        String email = "test@example.com";
        String rawPassword = "password123";
        Role expectedRole = new Role();
        expectedRole.setId(2);
        expectedRole.setName("USER");

        when(roleRepository.findByName("USER")).thenReturn(Optional.of(expectedRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        // Act
        User result = userService.register(email, rawPassword);

        // Assert
        assertEquals(expectedRole, result.getRole());
        verify(roleRepository).findByName("USER");
    }
} 