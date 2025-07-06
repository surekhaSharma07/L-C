package com.intimetec.newsaggregation.menu;

import com.intimetec.newsaggregation.app.ConsoleMenu;

public class SearchMenuPresenter implements MenuPresenter {
    private final ConsoleMenu console;

    public SearchMenuPresenter(ConsoleMenu console) {
        this.console = console;
    }

    @Override
    public void showMenu() throws Exception {
        console.getNewsHandler().showSearchMenu();
    }
}