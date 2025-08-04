package com.intimetec.newsaggregation.handlers;

import com.intimetec.newsaggregation.client.AuthClient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MenuFlowHandler {

    private static final String BASE_URL = "http://localhost:8081";

    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    private final AuthenticationHandler authenticationHandler;
    private final NewsHandler newsHandler;
    private final AdminHandler adminHandler;
    private final NotificationHandler notificationHandler;
    private final AuthClient authClient;

    public MenuFlowHandler(Scanner scanner, AuthenticationHandler authenticationHandler,
                           NewsHandler newsHandler, AdminHandler adminHandler,
                           NotificationHandler notificationHandler, AuthClient authClient) {
        this.scanner = scanner;
        this.authenticationHandler = authenticationHandler;
        this.newsHandler = newsHandler;
        this.adminHandler = adminHandler;
        this.notificationHandler = notificationHandler;
        this.authClient = authClient;
    }


    public void handleWelcomeMenu() throws Exception {
        while (true) {
            displayWelcomeMenu();
            String choice = getUserChoice();

            switch (choice) {
                case "1" -> {
                    if (handleLogin()) {
                        updateAdminClient();
                        if (authenticationHandler.isAdmin()) {
                            handleAdminMenu();
                        } else {
                            handleUserMenu();
                        }
                    }
                }
                case "2" -> authenticationHandler.handleSignup();
                case "3" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    public void handleUserMenu() throws Exception {
        while (true) {
            displayUserMenu();
            String choice = getUserChoice();

            if (!processUserChoice(choice)) {
                System.out.println("Logged out.");
                return;
            }
        }
    }

    public void handleAdminMenu() throws Exception {
        updateAdminHandler();

        while (true) {
            displayAdminMenu();
            String choice = getUserChoice();

            if (!processAdminChoice(choice)) {
                System.out.println("Logged out.");
                return;
            }
        }
    }

    private void displayWelcomeMenu() {
        System.out.print("\n1. Login\n2. Sign up\n3. Exit\n> ");
    }

    private void displayUserMenu() {
        System.out.printf("%nWelcome! Date: %s  Time: %s%n",
                LocalDate.now().format(dateFormatter),
                LocalTime.now().format(timeFormatter));
        System.out.print("""
                1. Headlines
                2. Saved Articles
                3. Search
                4. Notifications
                5. Logout
                > """);
    }

    private void displayAdminMenu() {
        System.out.println("""
                \nAdmin Options
                1. View external servers & status
                2. View external server details
                3. Add / Edit external server
                4. Add new News Category
                5. Article Visibility moderations
                6. Logout
                > """);
    }

    private String getUserChoice() {
        return scanner.nextLine().trim();
    }


    private boolean handleLogin() throws Exception {
        return authenticationHandler.handleLogin();
    }

    private boolean processUserChoice(String choice) throws Exception {
        switch (choice) {
            case "1" -> newsHandler.showHeadlinesMenu();
            case "2" -> newsHandler.showSavedArticlesMenu();
            case "3" -> newsHandler.showSearchMenu();
            case "4" -> notificationHandler.showNotificationsMenu();
            case "5" -> {
                return false;
            }
            default -> System.out.println("Invalid choice");
        }
        return true;
    }

    private boolean processAdminChoice(String choice) throws Exception {
        switch (choice) {
            case "1" -> adminHandler.viewExternalServers();
            case "2" -> adminHandler.viewExternalServerDetails();
            case "3" -> adminHandler.addEditExternalServer();
            case "4" -> adminHandler.addNewsCategory();
            case "5" -> adminHandler.manageArticleModeration();
            case "6" -> {
                return false;
            }
            default -> System.out.println("Invalid option");
        }
        return true;
    }

    private void updateAdminHandler() {
        String jwtToken = authClient.getJwtToken();
        adminHandler.updateAdminClient(jwtToken, BASE_URL);
    }

    private void updateAdminClient() {
        String jwtToken = authClient.getJwtToken();
        adminHandler.updateAdminClient(jwtToken, BASE_URL);
    }
} 