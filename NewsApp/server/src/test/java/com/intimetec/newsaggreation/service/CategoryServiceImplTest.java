package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.model.Category;
import com.intimetec.newsaggreation.repository.CategoryRepository;
import com.intimetec.newsaggreation.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category1;
    private Category category2;
    private Category newCategory;

    @BeforeEach
    void setUp() {
        category1 = new Category();
        category1.setId(1);
        category1.setName("Technology");

        category2 = new Category();
        category2.setId(2);
        category2.setName("Sports");

        newCategory = new Category();
        newCategory.setName("Science");
    }

    @Test
    void testFindAll_Success() {
        // Arrange
        List<Category> expectedCategories = Arrays.asList(category1, category2);
        when(categoryRepository.findAll()).thenReturn(expectedCategories);

        // Act
        List<Category> result = categoryService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Technology", result.get(0).getName());
        assertEquals("Sports", result.get(1).getName());
        verify(categoryRepository).findAll();
    }

    @Test
    void testFindAll_EmptyList() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Category> result = categoryService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(categoryRepository).findAll();
    }

    @Test
    void testFindById_Success() {
        // Arrange
        Integer id = 1;
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category1));

        // Act
        Category result = categoryService.findById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Technology", result.getName());
        verify(categoryRepository).findById(id);
    }

    @Test
    void testFindById_WhenCategoryNotFound() {
        // Arrange
        Integer id = 999;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            categoryService.findById(id)
        );

        assertEquals("Category not found: " + id, exception.getMessage());
        verify(categoryRepository).findById(id);
    }

    @Test
    void testCreate_Success() {
        // Arrange
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setId(3);
            return category;
        });

        // Act
        Category result = categoryService.create(newCategory);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getId());
        assertEquals("Science", result.getName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testCreate_WithNullCategory() {
        // Arrange
        Category nullCategory = null;
        when(categoryRepository.save(null)).thenThrow(new IllegalArgumentException("Category cannot be null"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            categoryService.create(nullCategory)
        );
    }

    @Test
    void testUpdate_Success() {
        // Arrange
        Integer id = 1;
        Category updateData = new Category();
        updateData.setName("Updated Technology");

        when(categoryRepository.findById(id)).thenReturn(Optional.of(category1));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            return category;
        });

        // Act
        Category result = categoryService.update(id, updateData);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Updated Technology", result.getName());
        verify(categoryRepository).findById(id);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testUpdate_WhenCategoryNotFound() {
        // Arrange
        Integer id = 999;
        Category updateData = new Category();
        updateData.setName("Updated Category");

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            categoryService.update(id, updateData)
        );

        assertEquals("Category not found: " + id, exception.getMessage());
        verify(categoryRepository).findById(id);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testDelete_Success() {
        // Arrange
        Integer id = 1;
        when(categoryRepository.findById(id)).thenReturn(Optional.of(category1));
        doNothing().when(categoryRepository).delete(any(Category.class));

        // Act
        categoryService.delete(id);

        // Assert
        verify(categoryRepository).findById(id);
        verify(categoryRepository).delete(category1);
    }

    @Test
    void testDelete_WhenCategoryNotFound() {
        // Arrange
        Integer id = 999;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            categoryService.delete(id)
        );

        assertEquals("Category not found: " + id, exception.getMessage());
        verify(categoryRepository).findById(id);
        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void testFindOrCreate_WhenCategoryExists() {
        // Arrange
        String categoryName = "Technology";
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category1));

        // Act
        Category result = categoryService.findOrCreate(categoryName);

        // Assert
        assertNotNull(result);
        assertEquals(category1.getId(), result.getId());
        assertEquals(categoryName, result.getName());
        verify(categoryRepository).findByName(categoryName);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testFindOrCreate_WhenCategoryDoesNotExist() {
        // Arrange
        String categoryName = "New Category";
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setId(4);
            return category;
        });

        // Act
        Category result = categoryService.findOrCreate(categoryName);

        // Assert
        assertNotNull(result);
        assertEquals(4, result.getId());
        assertEquals(categoryName, result.getName());
        verify(categoryRepository).findByName(categoryName);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testFindOrCreate_WithEmptyName() {
        // Arrange
        String categoryName = "";
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setId(5);
            return category;
        });

        // Act
        Category result = categoryService.findOrCreate(categoryName);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getId());
        assertEquals(categoryName, result.getName());
        verify(categoryRepository).findByName(categoryName);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testFindOrCreate_WithNullName() {
        // Arrange
        String categoryName = null;
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setId(6);
            return category;
        });

        // Act
        Category result = categoryService.findOrCreate(categoryName);

        // Assert
        assertNotNull(result);
        assertEquals(6, result.getId());
        assertNull(result.getName());
        verify(categoryRepository).findByName(categoryName);
        verify(categoryRepository).save(any(Category.class));
    }
} 