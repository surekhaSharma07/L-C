package com.intimetec.newsaggregation.menu;

import com.intimetec.newsaggregation.app.ConsoleMenu;

public class NotificationsMenuPresenter implements MenuPresenter {
    private final ConsoleMenu console;

    public NotificationsMenuPresenter(ConsoleMenu console) {
        this.console = console;
    }

    @Override
    public void showMenu() throws Exception {
        console.getNotificationHandler().showNotificationsMenu();
    }
}