package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.dto.ArticleResponse;
import com.intimetec.newsaggreation.model.Article;
import com.intimetec.newsaggreation.model.Reaction;
import com.intimetec.newsaggreation.model.ReactionType;
import com.intimetec.newsaggreation.model.SavedArticle;
import com.intimetec.newsaggreation.model.User;
import com.intimetec.newsaggreation.repository.ArticleRepository;
import com.intimetec.newsaggreation.repository.ReactionRepository;
import com.intimetec.newsaggreation.repository.SavedArticleRepository;
import com.intimetec.newsaggreation.repository.UserRepository;
import com.intimetec.newsaggreation.service.impl.UserArticleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class UserArticleServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private SavedArticleRepository savedArticleRepository;

    @Mock
    private ReactionRepository reactionRepository;

    @InjectMocks
    private UserArticleServiceImpl userArticleService;

    private User testUser;
    private Article testArticle;
    private SavedArticle testSavedArticle;
    private Reaction testReaction;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testArticle = new Article();
        testArticle.setId(1L);
        testArticle.setTitle("Test Article");
        testArticle.setUrl("https://example.com/article");
        testArticle.setPublishedAt(LocalDateTime.now());

        testSavedArticle = new SavedArticle();
        testSavedArticle.setId(1L);
        testSavedArticle.setUser(testUser);
        testSavedArticle.setNews(testArticle);

        testReaction = new Reaction();
        testReaction.setId(1L);
        testReaction.setUser(testUser);
        testReaction.setNews(testArticle);
        testReaction.setType(ReactionType.LIKE);
    }

    @Test
    void testSaveArticle_Success() {
        // Arrange
        Long userId = 1L;
        Long articleId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(testArticle));
        when(savedArticleRepository.save(any(SavedArticle.class))).thenReturn(testSavedArticle);

        // Act
        userArticleService.saveArticle(userId, articleId);

        // Assert
        verify(userRepository).findById(userId);
        verify(articleRepository).findById(articleId);
        verify(savedArticleRepository).save(any(SavedArticle.class));
    }

    @Test
    void testSaveArticle_UserNotFound() {
        // Arrange
        Long userId = 999L;
        Long articleId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            userArticleService.saveArticle(userId, articleId)
        );

        assertEquals("User not found: 999", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(articleRepository, never()).findById(any());
        verify(savedArticleRepository, never()).save(any());
    }

    @Test
    void testSaveArticle_ArticleNotFound() {
        // Arrange
        Long userId = 1L;
        Long articleId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            userArticleService.saveArticle(userId, articleId)
        );

        assertEquals("Article not found: 999", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(articleRepository).findById(articleId);
        verify(savedArticleRepository, never()).save(any());
    }

    @Test
    void testUnsaveArticle_Success() {
        // Arrange
        Long userId = 1L;
        Long articleId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(testArticle));
        doNothing().when(savedArticleRepository).deleteByUserAndNews(testUser, testArticle);

        // Act
        userArticleService.unsaveArticle(userId, articleId);

        // Assert
        verify(userRepository).findById(userId);
        verify(articleRepository).findById(articleId);
        verify(savedArticleRepository).deleteByUserAndNews(testUser, testArticle);
    }

    @Test
    void testUnsaveArticle_UserNotFound() {
        // Arrange
        Long userId = 999L;
        Long articleId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            userArticleService.unsaveArticle(userId, articleId)
        );

        assertEquals("User not found: 999", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(articleRepository, never()).findById(any());
        verify(savedArticleRepository, never()).deleteByUserAndNews(any(), any());
    }

    @Test
    void testListSavedArticles_Success() {
        // Arrange
        Long userId = 1L;
        SavedArticle savedArticle1 = new SavedArticle();
        savedArticle1.setUser(testUser);
        savedArticle1.setNews(testArticle);

        SavedArticle savedArticle2 = new SavedArticle();
        savedArticle2.setUser(testUser);
        Article article2 = new Article();
        article2.setId(2L);
        article2.setTitle("Second Article");
        article2.setPublishedAt(LocalDateTime.now());
        savedArticle2.setNews(article2);

        User otherUser = new User();
        otherUser.setId(2L);
        SavedArticle otherUserSavedArticle = new SavedArticle();
        otherUserSavedArticle.setUser(otherUser);
        otherUserSavedArticle.setNews(testArticle);

        List<SavedArticle> allSavedArticles = Arrays.asList(savedArticle1, savedArticle2, otherUserSavedArticle);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(savedArticleRepository.findAll()).thenReturn(allSavedArticles);

        // Act
        List<ArticleResponse> result = userArticleService.listSavedArticles(userId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testArticle.getTitle(), result.get(0).getTitle());
        assertEquals("Second Article", result.get(1).getTitle());
        verify(userRepository).findById(userId);
        verify(savedArticleRepository).findAll();
    }

    @Test
    void testListSavedArticles_UserNotFound() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            userArticleService.listSavedArticles(userId)
        );

        assertEquals("User not found: 999", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(savedArticleRepository, never()).findAll();
    }

    @Test
    void testListSavedArticles_EmptyList() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(savedArticleRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<ArticleResponse> result = userArticleService.listSavedArticles(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findById(userId);
        verify(savedArticleRepository).findAll();
    }

    @Test
    void testReactToArticle_NewReaction() {
        // Arrange
        Long userId = 1L;
        Long articleId = 1L;
        ReactionType reactionType = ReactionType.LIKE;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(testArticle));
        when(reactionRepository.findByUserAndNews(testUser, testArticle)).thenReturn(Optional.empty());
        when(reactionRepository.save(any(Reaction.class))).thenReturn(testReaction);

        // Act
        userArticleService.reactToArticle(userId, articleId, reactionType);

        // Assert
        verify(userRepository).findById(userId);
        verify(articleRepository).findById(articleId);
        verify(reactionRepository).findByUserAndNews(testUser, testArticle);
        verify(reactionRepository).save(any(Reaction.class));
    }

    @Test
    void testReactToArticle_UpdateExistingReaction() {
        // Arrange
        Long userId = 1L;
        Long articleId = 1L;
        ReactionType newReactionType = ReactionType.DISLIKE;

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(testArticle));
        when(reactionRepository.findByUserAndNews(testUser, testArticle)).thenReturn(Optional.of(testReaction));

        // Act
        userArticleService.reactToArticle(userId, articleId, newReactionType);

        // Assert
        verify(userRepository).findById(userId);
        verify(articleRepository).findById(articleId);
        verify(reactionRepository).findByUserAndNews(testUser, testArticle);
        verify(reactionRepository, never()).save(any(Reaction.class));
        assertEquals(newReactionType, testReaction.getType());
    }

    @Test
    void testReactToArticle_UserNotFound() {
        // Arrange
        Long userId = 999L;
        Long articleId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            userArticleService.reactToArticle(userId, articleId, ReactionType.LIKE)
        );

        assertEquals("User not found: 999", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(articleRepository, never()).findById(any());
        verify(reactionRepository, never()).findByUserAndNews(any(), any());
    }

    @Test
    void testRemoveReaction_Success() {
        // Arrange
        Long userId = 1L;
        Long articleId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(testArticle));
        doNothing().when(reactionRepository).deleteByUserAndNews(testUser, testArticle);

        // Act
        userArticleService.removeReaction(userId, articleId);

        // Assert
        verify(userRepository).findById(userId);
        verify(articleRepository).findById(articleId);
        verify(reactionRepository).deleteByUserAndNews(testUser, testArticle);
    }

    @Test
    void testRemoveReaction_UserNotFound() {
        // Arrange
        Long userId = 999L;
        Long articleId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            userArticleService.removeReaction(userId, articleId)
        );

        assertEquals("User not found: 999", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(articleRepository, never()).findById(any());
        verify(reactionRepository, never()).deleteByUserAndNews(any(), any());
    }

    @Test
    void testRemoveReaction_ArticleNotFound() {
        // Arrange
        Long userId = 1L;
        Long articleId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            userArticleService.removeReaction(userId, articleId)
        );

        assertEquals("Article not found: 999", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(articleRepository).findById(articleId);
        verify(reactionRepository, never()).deleteByUserAndNews(any(), any());
    }
} 