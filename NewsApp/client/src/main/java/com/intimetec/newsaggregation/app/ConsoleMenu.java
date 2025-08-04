package com.intimetec.newsaggregation.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intimetec.newsaggregation.client.*;
import com.intimetec.newsaggregation.menu.MenuPresenterFactory;
import com.intimetec.newsaggregation.menu.MenuType;
import com.intimetec.newsaggregation.handlers.AuthenticationHandler;
import com.intimetec.newsaggregation.handlers.NewsHandler;
import com.intimetec.newsaggregation.handlers.AdminHandler;
import com.intimetec.newsaggregation.handlers.NotificationHandler;
import com.intimetec.newsaggregation.handlers.MenuFlowHandler;

import java.util.Scanner;

public class ConsoleMenu {

    private static final String BASE_URL = "http://localhost:8081";

    private final Scanner scanner;
    private final ObjectMapper mapper;
    private final AuthClient authClient;
    private final NewsClient newsClient;
    private final NotificationClient notificationClient;
    private final UserArticleClient userArticleClient;
    private AdminClient adminClient;

    private final AuthenticationHandler authenticationHandler;
    private final NewsHandler newsHandler;
    private AdminHandler adminHandler;
    private final NotificationHandler notificationHandler;
    private final MenuFlowHandler menuFlowHandler;


    public ConsoleMenu() {
        ObjectMapper mapper = new ObjectMapper();
        AuthClient authClient = new AuthClient(mapper);
        NewsClient newsClient = new NewsClient(mapper, authClient);
        NotificationClient notificationClient = new NotificationClient(mapper, authClient);
        UserArticleClient userArticleClient = new UserArticleClient(authClient, mapper);
        this.scanner = new Scanner(System.in);
        this.mapper = mapper;
        this.authClient = authClient;
        this.newsClient = newsClient;
        this.notificationClient = notificationClient;
        this.userArticleClient = userArticleClient;
        this.authenticationHandler = new AuthenticationHandler(authClient, scanner);
        this.newsHandler = new NewsHandler(newsClient, userArticleClient, scanner);
        this.adminHandler = new AdminHandler(null, scanner);
        this.notificationHandler = new NotificationHandler(notificationClient, scanner);
        this.menuFlowHandler = new MenuFlowHandler(scanner, authenticationHandler,
                newsHandler, adminHandler,
                notificationHandler, authClient);
    }

    public NewsHandler getNewsHandler() {
        return newsHandler;
    }

    public NotificationHandler getNotificationHandler() {
        return notificationHandler;
    }

    public void start() throws Exception {
        MenuPresenterFactory.get(MenuType.WELCOME, this).showMenu();
    }

    public void welcomeMenu() throws Exception {
        menuFlowHandler.handleWelcomeMenu();
    }

    public void userMenuBody() throws Exception {
        menuFlowHandler.handleUserMenu();
    }

    public void adminMenuBody() throws Exception {
        menuFlowHandler.handleAdminMenu();
    }
}
