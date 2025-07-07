package com.intimetec.newsaggregation.menu;

import com.intimetec.newsaggregation.app.ConsoleMenu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuPresenterFactoryTest {

    @Mock
    private ConsoleMenu mockConsoleMenu;

    @BeforeEach
    void setUp() {
        reset(mockConsoleMenu);
    }

    @Test
    void testGet_WelcomeMenu() {

        MenuPresenter presenter = MenuPresenterFactory.get(MenuType.WELCOME, mockConsoleMenu);

        assertNotNull(presenter);
        assertTrue(presenter instanceof WelcomeMenuPresenter);
    }

    @Test
    void testGet_UserMenu() {
        MenuPresenter presenter = MenuPresenterFactory.get(MenuType.USER, mockConsoleMenu);

        assertNotNull(presenter);
        assertTrue(presenter instanceof UserMenuPresenter);
    }

    @Test
    void testGet_AdminMenu() {
        MenuPresenter presenter = MenuPresenterFactory.get(MenuType.ADMIN, mockConsoleMenu);

        assertNotNull(presenter);
        assertTrue(presenter instanceof AdminMenuPresenter);
    }

    @Test
    void testGet_HeadlineMenu() {
        MenuPresenter presenter = MenuPresenterFactory.get(MenuType.HEADLINE, mockConsoleMenu);

        assertNotNull(presenter);
        assertTrue(presenter instanceof HeadlineMenuPresenter);
    }

    @Test
    void testGet_SavedArticlesMenu() {
        MenuPresenter presenter = MenuPresenterFactory.get(MenuType.SAVED, mockConsoleMenu);

        assertNotNull(presenter);
        assertTrue(presenter instanceof SavedArticlesMenuPresenter);
    }

    @Test
    void testGet_SearchMenu() {
        MenuPresenter presenter = MenuPresenterFactory.get(MenuType.SEARCH, mockConsoleMenu);

        assertNotNull(presenter);
        assertTrue(presenter instanceof SearchMenuPresenter);
    }

    @Test
    void testGet_NotificationsMenu() {
        MenuPresenter presenter = MenuPresenterFactory.get(MenuType.NOTIFICATIONS, mockConsoleMenu);

        assertNotNull(presenter);
        assertTrue(presenter instanceof NotificationsMenuPresenter);
    }

    @Test
    void testGet_WithNullMenuType() {
        assertThrows(NullPointerException.class, () -> {
            MenuPresenterFactory.get(null, mockConsoleMenu);
        });
    }

    @Test
    void testMenuPresenterFactory_ConstructorNotAccessible() {
        assertThrows(Exception.class, () -> {
           MenuPresenterFactory.class.getDeclaredConstructor().newInstance();
        });
    }

    @Test
    void testAllMenuTypes_CreateValidPresenters() {
        MenuType[] menuTypes = MenuType.values();
        
        for (MenuType menuType : menuTypes) {
            MenuPresenter presenter = MenuPresenterFactory.get(menuType, mockConsoleMenu);

            assertNotNull(presenter, "Presenter should not be null for menu type: " + menuType);
            assertTrue(presenter instanceof MenuPresenter, 
                "Presenter should implement MenuPresenter interface for menu type: " + menuType);
        }
    }

    @Test
    void testMenuPresenterFactory_Consistency() {
        MenuPresenter presenter1 = MenuPresenterFactory.get(MenuType.WELCOME, mockConsoleMenu);
        MenuPresenter presenter2 = MenuPresenterFactory.get(MenuType.WELCOME, mockConsoleMenu);
        
        assertNotNull(presenter1);
        assertNotNull(presenter2);
        assertEquals(presenter1.getClass(), presenter2.getClass());
    }

    @Test
    void testMenuPresenterFactory_DifferentConsoleMenus() {
        ConsoleMenu mockConsoleMenu2 = mock(ConsoleMenu.class);
        
        MenuPresenter presenter1 = MenuPresenterFactory.get(MenuType.USER, mockConsoleMenu);
        MenuPresenter presenter2 = MenuPresenterFactory.get(MenuType.USER, mockConsoleMenu2);
        
        assertNotNull(presenter1);
        assertNotNull(presenter2);
        assertEquals(presenter1.getClass(), presenter2.getClass());
    }
} 