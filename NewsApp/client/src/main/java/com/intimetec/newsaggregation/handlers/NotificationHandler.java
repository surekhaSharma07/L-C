package com.intimetec.newsaggregation.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.intimetec.newsaggregation.client.NotificationClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NotificationHandler {

    private final NotificationClient notificationClient;
    private final Scanner scanner;

    public NotificationHandler(NotificationClient notificationClient, Scanner scanner) {
        this.notificationClient = notificationClient;
        this.scanner = scanner;
    }

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
        } catch (Exception exception) {
            System.out.println("Error fetching notifications: " + exception.getMessage());
        }
    }

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
                case "3" -> {
                    return;
                }
                case "4" -> {
                    System.out.println("Logged out.");
                    System.exit(0);
                }
                default -> System.out.println("Invalid option");
            }
        }
    }

    public void manageNotificationConfig() {
        try {
            JsonNode jsonNode = notificationClient.fetchConfig();
            while (true) {
                System.out.println(buildConfigScreen(jsonNode));
                String opt = scanner.nextLine().trim();
                switch (opt) {
                    case "1", "2", "3", "4" -> {
                        toggleCategory(jsonNode, opt);
                        jsonNode = notificationClient.saveConfig(jsonNode);
                    }
                    case "5" -> {
                        jsonNode = manageKeywords(jsonNode);
                        jsonNode = notificationClient.saveConfig(jsonNode);
                    }
                    case "6" -> {
                        return;
                    }
                    case "7" -> {
                        System.out.println("Logged out.");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid option");
                }
            }
        } catch (Exception exception) {
            System.out.println("Error managing notification config: " + exception.getMessage());
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

    private static String onOff(JsonNode config, String field) {
        return config.path(field).asBoolean() ? "Enabled" : "Disabled";
    }

    private void toggleCategory(JsonNode jsonNode, String option) {
        String field = switch (option) {
            case "1" -> "business";
            case "2" -> "entertainment";
            case "3" -> "sports";
            default -> "technology";
        };
        boolean current = jsonNode.path(field).asBoolean();
        ((ObjectNode) jsonNode).put(field, !current);
    }

    private JsonNode manageKeywords(JsonNode jsonNode) {
        List<String> keyword = new ArrayList<>();
        jsonNode.path("keywords").forEach(k -> keyword.add(k.path("term").asText()));

        while (true) {
            System.out.println("\nCurrent keywords: " + (keyword.isEmpty() ? "<none>" : keyword));
            System.out.print("1. Add  2. Remove  3. Done\n> ");
            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    System.out.print("Enter keyword: ");
                    String KeywordlowerCase = scanner.nextLine().trim().toLowerCase();
                    if (!KeywordlowerCase.isBlank() && !keyword.contains(KeywordlowerCase))
                        keyword.add(KeywordlowerCase);
                }
                case "2" -> {
                    System.out.print("Keyword to remove: ");
                    keyword.remove(scanner.nextLine().trim().toLowerCase());
                }
                case "3" -> {
                    var jsonNodes = new ObjectMapper().createArrayNode();
                    keyword.forEach(key -> jsonNodes.add(new com.fasterxml.jackson.databind.ObjectMapper().createObjectNode().putNull("id").put("term", key)));
                    ((ObjectNode) jsonNode).set("keywords", jsonNodes);
                    return jsonNode;
                }
                default -> System.out.println("Invalid");
            }
        }
    }
} 