package com.intimetec.newsaggregation.menu;

import com.intimetec.newsaggregation.app.ConsoleMenu;

public class WelcomeMenuPresenter implements MenuPresenter {

    private final ConsoleMenu console;
    public WelcomeMenuPresenter(ConsoleMenu console) { this.console = console; }

    @Override public void showMenu() throws Exception { console.welcomeMenu(); }
}
