package com.intimetec.newsaggregation.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intimetec.newsaggregation.client.NotificationClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles notification operations (view notifications, configure alerts).
 * Extracted from ConsoleMenu to follow Single Responsibility Principle.
 */
public class NotificationHandler {
    
    private final NotificationClient notificationClient;
    private final Scanner scanner;
    
    public NotificationHandler(NotificationClient notificationClient, Scanner scanner) {
        this.notificationClient = notificationClient;
        this.scanner = scanner;
    }
    
    /**
     * Displays user notifications.
     */
    public void displayNotifications() {
        try {
            List<JsonNode> notes = notificationClient.fetchNotifications();
            if (notes.isEmpty()) {
                System.out.println("\nNo notifications yet.\n");
                return;
            }

            System.out.println("\n--- Notifications ---");
            for (int i = 0; i < notes.size(); i++) {
                JsonNode n = notes.get(i);
                System.out.printf("%d. [%s] %s  (%s)%n   %s%n%n",
                        i + 1,
                        n.path("type").asText(""),
                        n.path("title").asText(""),
                        n.path("createdAt").asText("").replace('T', ' '),
                        n.path("url").asText(""));
            }
        } catch (Exception e) {
            System.out.println("Error fetching notifications: " + e.getMessage());
        }
    }
    
    /**
     * Shows the notifications menu.
     */
    public void showNotificationsMenu() {
        while (true) {
            System.out.print("""
                    \nN O T I F I C A T I O N S
                    1. View Notifications
                    2. Configure Notifications
                    3. Back
                    4. Logout
                    > """);

            switch (scanner.nextLine().trim()) {
                case "1" -> displayNotifications();
                case "2" -> manageNotificationConfig();
                case "3" -> { return; }
                case "4" -> { System.out.println("Logged out."); System.exit(0); }
                default -> System.out.println("Invalid option");
            }
        }
    }
    
    /**
     * Manages notification configuration.
     */
    public void manageNotificationConfig() {
        try {
            JsonNode cfg = notificationClient.fetchConfig();
            while (true) {
                System.out.println(buildConfigScreen(cfg));
                String opt = scanner.nextLine().trim();
                switch (opt) {
                    case "1", "2", "3", "4" -> {
                        toggleCategory(cfg, opt);
                        cfg = notificationClient.saveConfig(cfg);
                    }
                    case "5" -> {
                        cfg = manageKeywords(cfg);
                        cfg = notificationClient.saveConfig(cfg);
                    }
                    case "6" -> { return; }
                    case "7" -> { System.out.println("Logged out."); System.exit(0); }
                    default -> System.out.println("Invalid option");
                }
            }
        } catch (Exception e) {
            System.out.println("Error managing notification config: " + e.getMessage());
        }
    }
    
    private String buildConfigScreen(JsonNode cfg) {
        return String.format("""
                        \nC O N F I G U R E - N O T I F I C A T I O N S
                        1. Business      - %s
                        2. Entertainment - %s
                        3. Sports        - %s
                        4. Technology    - %s
                        5. Keywords      - %s
                        6. Back
                        7. Logout
                        Enter your option: """,
                onOff(cfg, "business"),
                onOff(cfg, "entertainment"),
                onOff(cfg, "sports"),
                onOff(cfg, "technology"),
                cfg.path("keywords").size() > 0 ? "Enabled" : "Disabled");
    }

    private static String onOff(JsonNode cfg, String field) {
        return cfg.path(field).asBoolean() ? "Enabled" : "Disabled";
    }

    private void toggleCategory(JsonNode cfg, String option) {
        String field = switch (option) {
            case "1" -> "business";
            case "2" -> "entertainment";
            case "3" -> "sports";
            default -> "technology";
        };
        boolean current = cfg.path(field).asBoolean();
        ((ObjectNode) cfg).put(field, !current);
    }

    private JsonNode manageKeywords(JsonNode cfg) {
        List<String> kw = new ArrayList<>();
        cfg.path("keywords").forEach(k -> kw.add(k.path("term").asText()));

        while (true) {
            System.out.println("\nCurrent keywords: " + (kw.isEmpty() ? "<none>" : kw));
            System.out.print("1. Add  2. Remove  3. Done\n> ");
            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    System.out.print("Enter keyword: ");
                    String k = scanner.nextLine().trim().toLowerCase();
                    if (!k.isBlank() && !kw.contains(k)) kw.add(k);
                }
                case "2" -> {
                    System.out.print("Keyword to remove: ");
                    kw.remove(scanner.nextLine().trim().toLowerCase());
                }
                case "3" -> {
                    var arr = new com.fasterxml.jackson.databind.ObjectMapper().createArrayNode();
                    kw.forEach(k -> arr.add(new com.fasterxml.jackson.databind.ObjectMapper().createObjectNode().putNull("id").put("term", k)));
                    ((ObjectNode) cfg).set("keywords", arr);
                    return cfg;
                }
                default -> System.out.println("Invalid");
            }
        }
    }
} 