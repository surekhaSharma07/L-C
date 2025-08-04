package com.intimetec.newsaggregation.menu;

import com.intimetec.newsaggregation.app.ConsoleMenu;

public class SavedArticlesMenuPresenter implements MenuPresenter {
    private final ConsoleMenu console;

    public SavedArticlesMenuPresenter(ConsoleMenu console) {
        this.console = console;
    }

    @Override
    public void showMenu() {
        console.getNewsHandler().showSavedArticlesMenu();
    }
}