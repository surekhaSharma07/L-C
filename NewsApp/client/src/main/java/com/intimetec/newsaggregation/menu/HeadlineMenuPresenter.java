package com.intimetec.newsaggregation.menu;

import com.intimetec.newsaggregation.app.ConsoleMenu;

public class HeadlineMenuPresenter implements MenuPresenter {
    private final ConsoleMenu console;

    public HeadlineMenuPresenter(ConsoleMenu console) {
        this.console = console;
    }

    @Override
    public void showMenu() throws Exception {
        console.getNewsHandler().showHeadlinesMenu();
    }
}