package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.model.Article;
import com.intimetec.newsaggreation.model.Category;
import com.intimetec.newsaggreation.repository.ArticleRepository;
import com.intimetec.newsaggreation.service.impl.ArticleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleServiceImpl articleService;

    private Article article1;
    private Article article2;
    private Article article3;
    private Category technologyCategory;
    private Category sportsCategory;

    @BeforeEach
    void setUp() {
        technologyCategory = new Category();
        technologyCategory.setId(1);
        technologyCategory.setName("Technology");

        sportsCategory = new Category();
        sportsCategory.setId(2);
        sportsCategory.setName("Sports");

        article1 = new Article();
        article1.setId(1L);
        article1.setTitle("Tech News 1");
        article1.setDescription("Latest technology updates");
        article1.setUrl("https://example.com/tech1");
        article1.setPrimaryCategory(technologyCategory);
        article1.setPublishedAt(LocalDateTime.now().minusDays(1));

        article2 = new Article();
        article2.setId(2L);
        article2.setTitle("Sports News 1");
        article2.setDescription("Latest sports updates");
        article2.setUrl("https://example.com/sports1");
        article2.setPrimaryCategory(sportsCategory);
        article2.setPublishedAt(LocalDateTime.now());

        article3 = new Article();
        article3.setId(3L);
        article3.setTitle("Tech News 2");
        article3.setDescription("More technology updates");
        article3.setUrl("https://example.com/tech2");
        article3.setPrimaryCategory(technologyCategory);
        article3.setPublishedAt(LocalDateTime.now().plusDays(1));
    }

    @Test
    void testExistsByUrl_WhenArticleExists() {
        // Arrange
        String url = "https://example.com/tech1";
        when(articleRepository.existsByUrl(url)).thenReturn(true);

        // Act
        boolean result = articleService.existsByUrl(url);

        // Assert
        assertTrue(result);
        verify(articleRepository).existsByUrl(url);
    }

    @Test
    void testExistsByUrl_WhenArticleDoesNotExist() {
        // Arrange
        String url = "https://example.com/nonexistent";
        when(articleRepository.existsByUrl(url)).thenReturn(false);

        // Act
        boolean result = articleService.existsByUrl(url);

        // Assert
        assertFalse(result);
        verify(articleRepository).existsByUrl(url);
    }

    @Test
    void testSave_Success() {
        // Arrange
        Article newArticle = new Article();
        newArticle.setTitle("New Article");
        newArticle.setDescription("New article description");
        newArticle.setUrl("https://example.com/new");

        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            article.setId(4L);
            return article;
        });

        // Act
        Article result = articleService.save(newArticle);

        // Assert
        assertNotNull(result);
        assertEquals(4L, result.getId());
        assertEquals("New Article", result.getTitle());
        verify(articleRepository).save(newArticle);
    }

    @Test
    void testFindAll_Success() {
        // Arrange
        List<Article> expectedArticles = Arrays.asList(article1, article2, article3);
        when(articleRepository.findAll()).thenReturn(expectedArticles);

        // Act
        List<Article> result = articleService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(articleRepository).findAll();
    }

    @Test
    void testFindAll_EmptyList() {
        // Arrange
        when(articleRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Article> result = articleService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(articleRepository).findAll();
    }

    @Test
    void testFindToday_Success() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<Article> todayArticles = Arrays.asList(article2);

        when(articleRepository.findAllByPublishedAtBetween(start, end)).thenReturn(todayArticles);

        // Act
        List<Article> result = articleService.findToday();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sports News 1", result.get(0).getTitle());
        verify(articleRepository).findAllByPublishedAtBetween(start, end);
    }

    @Test
    void testFindById_Success() {
        // Arrange
        Long id = 1L;
        when(articleRepository.findById(id)).thenReturn(Optional.of(article1));

        // Act
        Article result = articleService.findById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Tech News 1", result.getTitle());
        verify(articleRepository).findById(id);
    }

    @Test
    void testFindById_WhenArticleNotFound() {
        // Arrange
        Long id = 999L;
        when(articleRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            articleService.findById(id)
        );

        assertEquals("News not found: " + id, exception.getMessage());
        verify(articleRepository).findById(id);
    }

    @Test
    void testFindByPrimaryCategory_Success() {
        // Arrange
        String categoryName = "Technology";
        List<Article> techArticles = Arrays.asList(article1, article3);
        when(articleRepository.findByPrimaryCategory_Name(categoryName)).thenReturn(techArticles);

        // Act
        List<Article> result = articleService.findByPrimaryCategory(categoryName);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Technology", result.get(0).getPrimaryCategory().getName());
        verify(articleRepository).findByPrimaryCategory_Name(categoryName);
    }

    @Test
    void testFindByCategory_Success() {
        // Arrange
        String categoryName = "Sports";
        List<Article> sportsArticles = Arrays.asList(article2);
        when(articleRepository.findByCategories_Name(categoryName)).thenReturn(sportsArticles);

        // Act
        List<Article> result = articleService.findByCategory(categoryName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sports News 1", result.get(0).getTitle());
        verify(articleRepository).findByCategories_Name(categoryName);
    }

    @Test
    void testFindByDateAndCategory_Success() {
        // Arrange
        LocalDate date = LocalDate.now();
        String categoryName = "Technology";
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<Article> articles = Arrays.asList(article1);

        when(articleRepository.findAllByPublishedAtBetweenAndPrimaryCategory_Name(start, end, categoryName))
            .thenReturn(articles);

        // Act
        List<Article> result = articleService.findByDateAndCategory(date, categoryName);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tech News 1", result.get(0).getTitle());
        verify(articleRepository).findAllByPublishedAtBetweenAndPrimaryCategory_Name(start, end, categoryName);
    }

    @Test
    void testFindByDateRangeAndCategory_Success() {
        // Arrange
        LocalDate from = LocalDate.now().minusDays(1);
        LocalDate to = LocalDate.now().plusDays(1);
        String categoryName = "Technology";
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();
        List<Article> articles = Arrays.asList(article1, article3);

        when(articleRepository.findAllByPublishedAtBetweenAndCategories_Name(start, end, categoryName))
            .thenReturn(articles);

        // Act
        List<Article> result = articleService.findByDateRangeAndCategory(from, to, categoryName);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(articleRepository).findAllByPublishedAtBetweenAndCategories_Name(start, end, categoryName);
    }

    @Test
    void testFindByDateRange_Success() {
        // Arrange
        LocalDate from = LocalDate.now().minusDays(1);
        LocalDate to = LocalDate.now().plusDays(1);
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();
        List<Article> articles = Arrays.asList(article1, article2, article3);

        when(articleRepository.findAllByPublishedAtBetween(start, end)).thenReturn(articles);

        // Act
        List<Article> result = articleService.findByDateRange(from, to);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(articleRepository).findAllByPublishedAtBetween(start, end);
    }

    @Test
    void testSearch_Success() {
        // Arrange
        String query = "technology";
        List<Article> searchResults = Arrays.asList(article1, article3);
        when(articleRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query))
            .thenReturn(searchResults);

        // Act
        List<Article> result = articleService.search(query);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(articleRepository).findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

    @Test
    void testSearch_WithEmptyQuery() {
        // Arrange
        String query = "";
        List<Article> searchResults = Arrays.asList();
        when(articleRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query))
            .thenReturn(searchResults);

        // Act
        List<Article> result = articleService.search(query);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(articleRepository).findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

    @Test
    void testSearch_WithNullQuery() {
        // Arrange
        String query = null;
        List<Article> searchResults = Arrays.asList();
        when(articleRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query))
            .thenReturn(searchResults);

        // Act
        List<Article> result = articleService.search(query);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(articleRepository).findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

    @Test
    void testFindByDateRange_WithSameDate() {
        // Arrange
        LocalDate date = LocalDate.now();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        List<Article> articles = Arrays.asList(article2);

        when(articleRepository.findAllByPublishedAtBetween(start, end)).thenReturn(articles);

        // Act
        List<Article> result = articleService.findByDateRange(date, date);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(articleRepository).findAllByPublishedAtBetween(start, end);
    }
} 