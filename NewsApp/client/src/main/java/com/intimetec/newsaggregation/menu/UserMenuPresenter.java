package com.intimetec.newsaggregation.menu;

import com.intimetec.newsaggregation.app.ConsoleMenu;

public class UserMenuPresenter implements MenuPresenter {
    private final ConsoleMenu console;

    public UserMenuPresenter(ConsoleMenu console) {
        this.console = console;
    }

    @Override
    public void showMenu() throws Exception {
        console.userMenuBody();
    }
}
