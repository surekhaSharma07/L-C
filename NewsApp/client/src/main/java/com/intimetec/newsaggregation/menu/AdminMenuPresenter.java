package com.intimetec.newsaggregation.menu;

import com.intimetec.newsaggregation.app.ConsoleMenu;

public class AdminMenuPresenter implements MenuPresenter {
    private final ConsoleMenu console;
    public AdminMenuPresenter(ConsoleMenu console) { this.console = console; }

    @Override public void showMenu() throws Exception { console.adminMenuBody(); }
}
