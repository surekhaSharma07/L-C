package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.dto.NotificationDto;
import com.intimetec.newsaggreation.model.Article;
import com.intimetec.newsaggreation.model.Notification;
import com.intimetec.newsaggreation.model.User;
import com.intimetec.newsaggreation.repository.ArticleRepository;
import com.intimetec.newsaggreation.repository.NotificationRepository;
import com.intimetec.newsaggreation.repository.UserRepository;
import com.intimetec.newsaggreation.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User testUser;
    private Article testArticle;
    private Notification notification1;
    private Notification notification2;
    private Notification notification3;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testArticle = new Article();
        testArticle.setId(1L);
        testArticle.setTitle("Test Article Title");
        testArticle.setUrl("https://example.com/article1");

        notification1 = new Notification();
        notification1.setId(1L);
        notification1.setUser(testUser);
        notification1.setNewsId(1L);
        notification1.setType("ALERT");
        notification1.setSentAt(LocalDateTime.now().minusHours(1));

        notification2 = new Notification();
        notification2.setId(2L);
        notification2.setUser(testUser);
        notification2.setNewsId(2L);
        notification2.setType("UPDATE");
        notification2.setSentAt(LocalDateTime.now().minusHours(2));

        notification3 = new Notification();
        notification3.setId(3L);
        notification3.setUser(testUser);
        notification3.setNewsId(999L);
        notification3.setType("ALERT");
        notification3.setSentAt(LocalDateTime.now().minusHours(3));
    }

    @Test
    void testFindByUserEmail_Success() {
        String email = "test@example.com";
        List<Notification> notifications = Arrays.asList(notification1, notification2);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(notificationRepository.findByUserOrderBySentAtDesc(testUser)).thenReturn(notifications);
        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
        when(articleRepository.findById(2L)).thenReturn(Optional.of(testArticle));

        List<NotificationDto> result = notificationService.findByUserEmail(email);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("ALERT", result.get(0).type());
        assertEquals("Test Article Title", result.get(0).title());
        assertEquals("https://example.com/article1", result.get(0).url());
        assertNotNull(result.get(0).createdAt());

        verify(userRepository).findByEmail(email);
        verify(notificationRepository).findByUserOrderBySentAtDesc(testUser);
        verify(articleRepository, times(2)).findById(any());
    }

    @Test
    void testFindByUserEmail_WhenUserNotFound() {
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            notificationService.findByUserEmail(email)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(notificationRepository, never()).findByUserOrderBySentAtDesc(any());
        verify(articleRepository, never()).findById(any());
    }

    @Test
    void testFindByUserEmail_WithDeletedArticle() {
        String email = "test@example.com";
        List<Notification> notifications = Arrays.asList(notification3); // Notification with non-existent article

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(notificationRepository.findByUserOrderBySentAtDesc(testUser)).thenReturn(notifications);
        when(articleRepository.findById(999L)).thenReturn(Optional.empty());

        List<NotificationDto> result = notificationService.findByUserEmail(email);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).id());
        assertEquals("ALERT", result.get(0).type());
        assertEquals("(article deleted)", result.get(0).title());
        assertEquals("", result.get(0).url());
        assertNotNull(result.get(0).createdAt());

        verify(userRepository).findByEmail(email);
        verify(notificationRepository).findByUserOrderBySentAtDesc(testUser);
        verify(articleRepository).findById(999L);
    }

    @Test
    void testFindByUserEmail_EmptyNotifications() {
        String email = "test@example.com";
        List<Notification> notifications = Arrays.asList();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(notificationRepository.findByUserOrderBySentAtDesc(testUser)).thenReturn(notifications);

        List<NotificationDto> result = notificationService.findByUserEmail(email);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findByEmail(email);
        verify(notificationRepository).findByUserOrderBySentAtDesc(testUser);
        verify(articleRepository, never()).findById(any());
    }

    @Test
    void testFindByUserEmail_MixedArticles() {
        String email = "test@example.com";
        List<Notification> notifications = Arrays.asList(notification1, notification3); // One with article, one without

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(notificationRepository.findByUserOrderBySentAtDesc(testUser)).thenReturn(notifications);
        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
        when(articleRepository.findById(999L)).thenReturn(Optional.empty());

        List<NotificationDto> result = notificationService.findByUserEmail(email);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Test Article Title", result.get(0).title());
        assertEquals("https://example.com/article1", result.get(0).url());

        assertEquals("(article deleted)", result.get(1).title());
        assertEquals("", result.get(1).url());

        verify(userRepository).findByEmail(email);
        verify(notificationRepository).findByUserOrderBySentAtDesc(testUser);
        verify(articleRepository, times(2)).findById(any());
    }

    @Test
    void testFindByUserEmail_WithNullEmail() {
        String email = null;
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            notificationService.findByUserEmail(email)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(notificationRepository, never()).findByUserOrderBySentAtDesc(any());
    }

    @Test
    void testFindByUserEmail_WithEmptyEmail() {
        String email = "";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            notificationService.findByUserEmail(email)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(notificationRepository, never()).findByUserOrderBySentAtDesc(any());
    }
} 