package com.intimetec.newsaggregation.menu;

import com.intimetec.newsaggregation.app.ConsoleMenu;

public final class MenuPresenterFactory {

    private MenuPresenterFactory() { /* no‑instantiation */ }

    public static MenuPresenter get(MenuType type, ConsoleMenu console) {
        return switch (type) {
            case WELCOME       -> new WelcomeMenuPresenter(console);
            case USER          -> new UserMenuPresenter(console);
            case ADMIN         -> new AdminMenuPresenter(console);
            case HEADLINE      -> new HeadlineMenuPresenter(console);
            case SAVED         -> new SavedArticlesMenuPresenter(console);
            case SEARCH        -> new SearchMenuPresenter(console);
            case NOTIFICATIONS -> new NotificationsMenuPresenter(console);
        };
    }
}
