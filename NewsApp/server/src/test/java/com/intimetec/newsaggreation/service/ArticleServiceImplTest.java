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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleServiceImpl articleService;

    private Article firstArticle;
    private Article secondArticle;
    private Article thirdArticle;
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

        firstArticle = new Article();
        firstArticle.setId(1L);
        firstArticle.setTitle("Tech News 1");
        firstArticle.setDescription("Latest technology updates");
        firstArticle.setUrl("https://example.com/tech1");
        firstArticle.setPrimaryCategory(technologyCategory);
        firstArticle.setPublishedAt(LocalDateTime.now().minusDays(1));

        secondArticle = new Article();
        secondArticle.setId(2L);
        secondArticle.setTitle("Sports News 1");
        secondArticle.setDescription("Latest sports updates");
        secondArticle.setUrl("https://example.com/sports1");
        secondArticle.setPrimaryCategory(sportsCategory);
        secondArticle.setPublishedAt(LocalDateTime.now());

        thirdArticle = new Article();
        thirdArticle.setId(3L);
        thirdArticle.setTitle("Tech News 2");
        thirdArticle.setDescription("More technology updates");
        thirdArticle.setUrl("https://example.com/tech2");
        thirdArticle.setPrimaryCategory(technologyCategory);
        thirdArticle.setPublishedAt(LocalDateTime.now().plusDays(1));
    }

    @Test
    void testExistsByUrl_WhenArticleExists() {
        String url = "https://example.com/tech1";
        when(articleRepository.existsByUrl(url)).thenReturn(true);

        boolean result = articleService.existsByUrl(url);

        assertTrue(result);
        verify(articleRepository).existsByUrl(url);
    }

    @Test
    void testExistsByUrl_WhenArticleDoesNotExist() {
        String url = "https://example.com/nonexistent";
        when(articleRepository.existsByUrl(url)).thenReturn(false);
        boolean result = articleService.existsByUrl(url);

        assertFalse(result);
        verify(articleRepository).existsByUrl(url);
    }

    @Test
    void testSave_Success() {
        Article newArticle = new Article();
        newArticle.setTitle("New Article");
        newArticle.setDescription("New article description");
        newArticle.setUrl("https://example.com/new");

        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> {
            Article article = invocation.getArgument(0);
            article.setId(4L);
            return article;
        });

        Article result = articleService.save(newArticle);

        assertNotNull(result);
        assertEquals(4L, result.getId());
        assertEquals("New Article", result.getTitle());
        verify(articleRepository).save(newArticle);
    }

    @Test
    void testFindAll_Success() {
        List<Article> expectedArticles = Arrays.asList(firstArticle, secondArticle, thirdArticle);
        when(articleRepository.findAll()).thenReturn(expectedArticles);

        List<Article> result = articleService.findAll();

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(articleRepository).findAll();
    }

    @Test
    void testFindAll_EmptyList() {
        when(articleRepository.findAll()).thenReturn(Arrays.asList());

        List<Article> result = articleService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(articleRepository).findAll();
    }

    @Test
    void testFindToday_Success() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<Article> todayArticles = Arrays.asList(secondArticle);

        when(articleRepository.findAllByPublishedAtBetween(start, end)).thenReturn(todayArticles);

        List<Article> result = articleService.findToday();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sports News 1", result.get(0).getTitle());
        verify(articleRepository).findAllByPublishedAtBetween(start, end);
    }

    @Test
    void testFindById_Success() {
        Long id = 1L;
        when(articleRepository.findById(id)).thenReturn(Optional.of(firstArticle));

        Article result = articleService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Tech News 1", result.getTitle());
        verify(articleRepository).findById(id);
    }

    @Test
    void testFindById_WhenArticleNotFound() {
        Long id = 999L;
        when(articleRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                articleService.findById(id)
        );
        assertEquals("News not found: " + id, exception.getMessage());
        verify(articleRepository).findById(id);
    }

    @Test
    void testFindByPrimaryCategory_Success() {
        String categoryName = "Technology";
        List<Article> techArticles = Arrays.asList(firstArticle, thirdArticle);
        when(articleRepository.findByPrimaryCategory_Name(categoryName)).thenReturn(techArticles);

        List<Article> result = articleService.findByPrimaryCategory(categoryName);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Technology", result.get(0).getPrimaryCategory().getName());
        verify(articleRepository).findByPrimaryCategory_Name(categoryName);
    }

    @Test
    void testFindByCategory_Success() {
        String categoryName = "Sports";
        List<Article> sportsArticles = Arrays.asList(secondArticle);
        when(articleRepository.findByCategories_Name(categoryName)).thenReturn(sportsArticles);

        List<Article> result = articleService.findByCategory(categoryName);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sports News 1", result.get(0).getTitle());
        verify(articleRepository).findByCategories_Name(categoryName);
    }

    @Test
    void testFindByDateAndCategory_Success() {
        LocalDate date = LocalDate.now();
        String categoryName = "Technology";
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        List<Article> articles = Arrays.asList(firstArticle);

        when(articleRepository.findAllByPublishedAtBetweenAndPrimaryCategory_Name(start, end, categoryName))
                .thenReturn(articles);

        List<Article> result = articleService.findByDateAndCategory(date, categoryName);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tech News 1", result.get(0).getTitle());
        verify(articleRepository).findAllByPublishedAtBetweenAndPrimaryCategory_Name(start, end, categoryName);
    }

    @Test
    void testFindByDateRangeAndCategory_Success() {
        LocalDate from = LocalDate.now().minusDays(1);
        LocalDate to = LocalDate.now().plusDays(1);
        String categoryName = "Technology";
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();
        List<Article> articles = Arrays.asList(firstArticle, thirdArticle);

        when(articleRepository.findAllByPublishedAtBetweenAndCategories_Name(start, end, categoryName))
                .thenReturn(articles);

        List<Article> result = articleService.findByDateRangeAndCategory(from, to, categoryName);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(articleRepository).findAllByPublishedAtBetweenAndCategories_Name(start, end, categoryName);
    }

    @Test
    void testFindByDateRange_Success() {
        LocalDate from = LocalDate.now().minusDays(1);
        LocalDate to = LocalDate.now().plusDays(1);
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();
        List<Article> articles = Arrays.asList(firstArticle, secondArticle, thirdArticle);

        when(articleRepository.findAllByPublishedAtBetween(start, end)).thenReturn(articles);

        List<Article> result = articleService.findByDateRange(from, to);

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(articleRepository).findAllByPublishedAtBetween(start, end);
    }

    @Test
    void testSearch_Success() {
        String query = "technology";
        List<Article> searchResults = Arrays.asList(firstArticle, thirdArticle);
        when(articleRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query))
                .thenReturn(searchResults);

        List<Article> result = articleService.search(query);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(articleRepository).findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

    @Test
    void testSearch_WithEmptyQuery() {
        String query = "";
        List<Article> searchResults = Arrays.asList();
        when(articleRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query))
                .thenReturn(searchResults);

        List<Article> result = articleService.search(query);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(articleRepository).findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

    @Test
    void testSearch_WithNullQuery() {
        String query = null;
        List<Article> searchResults = Arrays.asList();
        when(articleRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query))
                .thenReturn(searchResults);

        List<Article> result = articleService.search(query);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(articleRepository).findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

    @Test
    void testFindByDateRange_WithSameDate() {
        LocalDate date = LocalDate.now();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        List<Article> articles = Arrays.asList(secondArticle);

        when(articleRepository.findAllByPublishedAtBetween(start, end)).thenReturn(articles);

        List<Article> result = articleService.findByDateRange(date, date);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(articleRepository).findAllByPublishedAtBetween(start, end);
    }
} 