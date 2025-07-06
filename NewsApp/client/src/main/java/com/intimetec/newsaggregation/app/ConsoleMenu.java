package com.intimetec.newsaggregation.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intimetec.newsaggregation.client.*;

import com.intimetec.newsaggregation.menu.MenuPresenterFactory;
import com.intimetec.newsaggregation.menu.MenuType;
import com.intimetec.newsaggregation.handlers.AuthenticationHandler;
import com.intimetec.newsaggregation.handlers.NewsHandler;
import com.intimetec.newsaggregation.handlers.AdminHandler;
import com.intimetec.newsaggregation.handlers.NotificationHandler;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ConsoleMenu {

    /* ───────── Constants ───────── */
    private static final String BASE_URL = "http://localhost:8081";

    /* ───────── Shared state & helpers ───────── */
    private final Scanner scanner = new Scanner(System.in);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

    private final ObjectMapper mapper = new ObjectMapper();
    private final AuthClient authClient = new AuthClient(mapper);
    private final NewsClient newsClient = new NewsClient(mapper, authClient);
    private final NotificationClient notificationClient = new NotificationClient(mapper, authClient);
    private final UserArticleClient userArticleClient = new UserArticleClient(authClient, mapper);
    private  AdminClient adminClient;
    // Handler fields
    private final AuthenticationHandler authenticationHandler;
    private final NewsHandler newsHandler;
    private AdminHandler adminHandler; // Not final since we update it after login
    private final NotificationHandler notificationHandler;


    public NewsHandler getNewsHandler() {
        return newsHandler;
    }

    public NotificationHandler getNotificationHandler() {
        return notificationHandler;
    }

    public ConsoleMenu() {
        authenticationHandler = new AuthenticationHandler(authClient, scanner);
        newsHandler = new NewsHandler(newsClient, userArticleClient, scanner);
        adminHandler = new AdminHandler(null, scanner); // adminClient will be updated after login
        notificationHandler = new NotificationHandler(notificationClient, scanner);
    }

    /* ────────── PUBLIC ENTRY FROM main() ────────── */
    public void start() throws Exception {
        MenuPresenterFactory.get(MenuType.WELCOME, this).showMenu();
    }

    /* =================================================================== */
    /*  W E L C O M E   M E N U  (login / signup / exit)                   */
    /* =================================================================== */
    public void welcomeMenu() throws Exception {
        while (true) {
            System.out.print("\n1. Login\n2. Sign up\n3. Exit\n> ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    if (authenticationHandler.handleLogin()) {
                        updateAdminClient();
                        if (authenticationHandler.isAdmin())
                            adminMenuBody();
                        else
                            userMenuBody();
                    }
                }
                case "2" -> authenticationHandler.handleSignup();
                case "3" -> {
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    /* =================================================================== */
    /*  U S E R   T O P - L E V E L   M E N U                              */
    /* =================================================================== */
    public void userMenuBody() throws Exception {
        while (true) {
            displayUserMenuHeader();
            String choice = scanner.nextLine().trim();
            if (!handleUserMenuChoice(choice)) {
                return;
            }
        }
    }

    /**
     * Displays the user menu header with current date and time.
     */
    private void displayUserMenuHeader() {
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

    /**
     * Handles user menu choice selection.
     *
     * @return true if should continue, false if should exit
     */
    private boolean handleUserMenuChoice(String choice) throws Exception {
        switch (choice) {
            case "1" -> newsHandler.showHeadlinesMenu();
            case "2" -> newsHandler.showSavedArticlesMenu();
            case "3" -> newsHandler.showSearchMenu();
            case "4" -> notificationHandler.showNotificationsMenu();
            case "5" -> {
                System.out.println("Logged out.");
                return false;
            }
            default -> System.out.println("Invalid");
        }
        return true;
    }

    /* =================================================================== */
    /*  A D M I N   M E N U                                                */
    /* =================================================================== */
    public void adminMenuBody() throws Exception {
        // Update admin handler with current JWT token
        updateAdminHandler();

        while (true) {
            displayAdminMenu();
            String choice = scanner.nextLine().trim();
            if (!handleAdminMenuChoice(choice)) {
                return;
            }
        }
    }

    /**
     * Displays the admin menu options.
     */
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

    /**
     * Handles admin menu choice selection.
     *
     * @return true if should continue, false if should exit
     */
    private boolean handleAdminMenuChoice(String choice) throws Exception {
        switch (choice) {
            case "1" -> adminHandler.viewExternalServers();
            case "2" -> adminHandler.viewExternalServerDetails();
            case "3" -> adminHandler.addEditExternalServer();
            case "4" -> adminHandler.addNewsCategory();
            case "5" -> adminHandler.manageArticleModeration();
            case "6" -> {
                System.out.println("Logged out.");
                return false;
            }
            default -> System.out.println("Invalid option");
        }
        return true;
    }

    /**
     * Updates the admin handler with current JWT token.
     */
    private void updateAdminHandler() {
        String jwtToken = authClient.getJwtToken();
        adminHandler.updateAdminClient(jwtToken, BASE_URL);
    }


    // Helper method to update AdminClient with current JWT token
    private void updateAdminClient() {
        String jwtToken = authClient.getJwtToken();
        adminHandler.updateAdminClient(jwtToken, BASE_URL);
        adminClient = new AdminClient(BASE_URL, jwtToken);
    }

}
