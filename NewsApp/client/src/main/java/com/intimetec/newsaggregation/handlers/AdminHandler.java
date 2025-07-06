package com.intimetec.newsaggregation.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.intimetec.newsaggregation.client.AdminClient;
import com.intimetec.newsaggregation.client.AdminClient.ApiSourceDto;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Handles admin operations (view reports, manage API sources).
 * Extracted from ConsoleMenu to follow Single Responsibility Principle.
 */
public class AdminHandler {

    private AdminClient adminClient;
    private final Scanner scanner;

    public AdminHandler(AdminClient adminClient, Scanner scanner) {
        this.adminClient = adminClient;
        this.scanner = scanner;
    }

    /**
     * Updates the AdminClient with current JWT token.
     */
    public void updateAdminClient(String jwtToken, String baseUrl) {
        if (jwtToken != null && !jwtToken.trim().isEmpty()) {
            this.adminClient = new AdminClient(baseUrl, jwtToken);
        } else {
            System.out.println("Warning: JWT token is null or empty. Please log in again.");
        }
    }

    /**
     * Displays article reports.
     */
    public void displayArticleReports() {
        try {
            long articleId = getArticleIdFromUser();
            AdminClient.ReportDto[] reports = adminClient.fetchReports(articleId);

            if (reports.length == 0) {
                System.out.println("\nNo reports found for this article.\n");
                return;
            }

            displayReportsList(reports);

        } catch (NumberFormatException e) {
            System.out.println("Invalid article ID format.");
        } catch (Exception e) {
            System.out.println("Error fetching reports: " + e.getMessage());
        }
    }

    /**
     * Gets article ID from user input.
     */
    private long getArticleIdFromUser() {
        System.out.print("Enter article ID to view reports: ");
        return Long.parseLong(scanner.nextLine().trim());
    }

    /**
     * Displays the list of reports for an article.
     */
    private void displayReportsList(AdminClient.ReportDto[] reports) {
        System.out.println("\nARTICLE REPORTS (" + reports.length + ")");
        for (AdminClient.ReportDto report : reports) {
            System.out.printf("User: %s%n", report.getUserEmail());
            System.out.printf("Reason: %s%n", report.getReason());
            System.out.printf("Reported At: %s%n", report.getReportedAt());
            System.out.println("---");
        }
    }

    /**
     * Views external servers and their status.
     */
    public void viewExternalServers() {
        try {
            ApiSourceDto[] list = adminClient.listApiSources();
            System.out.println("\n--- External Servers ---");
            for (ApiSourceDto s : list)
                System.out.printf("[%d] %-18s %s%n",
                        s.getId(), s.getName(), s.getStatus());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Views detailed information about external servers.
     */
    public void viewExternalServerDetails() {
        try {
            System.out.print("Server id: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            ApiSourceDto d = adminClient.getApiSource(id);
            if (d == null || d.getId() == null)
                System.out.println("No server found with ID " + id);
            else
                System.out.printf("""
                                \nId: %d
                                Name: %s
                                Endpoint: %s
                                API Key: %s
                                Polling: %d
                                Status: %s
                                """, d.getId(), d.getName(),
                        d.getEndpointUrl(), d.getApiKey(),
                        d.getPollingFreq(), d.getStatus());
        } catch (NumberFormatException e) {
            System.out.println("Invalid server ID format.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Adds or edits external servers.
     */
    public void addEditExternalServer() {
        try {
            ApiSourceDto dto = new ApiSourceDto();
            System.out.print("Id (blank=new): ");
            String idTxt = scanner.nextLine().trim();
            if (!idTxt.isBlank()) dto.setId(Integer.parseInt(idTxt));

            System.out.print("Name        : ");
            dto.setName(scanner.nextLine().trim());
            System.out.print("Endpoint URL: ");
            dto.setEndpointUrl(scanner.nextLine().trim());
            System.out.print("API Key     : ");
            dto.setApiKey(scanner.nextLine().trim());
            System.out.print("Polling freq: ");
            String freq = scanner.nextLine().trim();
            if (!freq.isBlank()) dto.setPollingFreq(Integer.parseInt(freq));

            System.out.println("Saved => " + adminClient.saveApiSource(dto));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Adds a new news category.
     */
    public void addNewsCategory() {
        try {
            System.out.print("New category name: ");
            System.out.println("Category added: " +
                    adminClient.addCategory(scanner.nextLine().trim()));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    /**
     * Manages article moderation.
     */
    public void manageArticleModeration() {
        while (true) {
            System.out.print("""    
                    M O D E R A T E   A R T I C L E S
                    1. Hide a single article
                    2. Un‑hide a single article
                    3. Hide all in a category
                    4. Un‑hide all in a category
                    5. View all reports for one article
                    6. Back
                    > """);

            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    System.out.print("Article‑ID to hide : ");
                    try {
                        adminClient.hideArticle(Long.parseLong(scanner.nextLine().trim()));
                        System.out.println("✔ hidden");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case "2" -> {
                    System.out.print("Article‑ID to unhide: ");
                    try {
                        adminClient.unhideArticle(Long.parseLong(scanner.nextLine().trim()));
                        System.out.println("✔ visible again");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case "3" -> {
                    System.out.print("Category‑ID to hide : ");
                    try {
                        adminClient.hideCategory(Integer.parseInt(scanner.nextLine().trim()));
                        System.out.println("✔ all hidden");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case "4" -> {
                    System.out.print("Category‑ID to unhide: ");
                    try {
                        adminClient.unhideCategory(Integer.parseInt(scanner.nextLine().trim()));
                        System.out.println("✔ all visible");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case "5" -> {
                    System.out.print("Article‑ID : ");
                    try {
                        long id = Long.parseLong(scanner.nextLine().trim());
                        AdminClient.ReportDto[] reports = adminClient.fetchReports(id);
                        if (reports == null || reports.length == 0) {
                            System.out.println("\n(no reports)\n");
                        } else {
                            System.out.println("\n--- REPORTS ---");
                            for (AdminClient.ReportDto r : reports) {
                                String at = r.getReportedAt() == null ? "‑" : r.getReportedAt().toString().replace('T', ' ');
                                System.out.printf("- %s  (%s)  – \"%s\"%n",
                                        r.getUserEmail(), at, r.getReason());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case "6" -> {
                    return;
                }
                default -> System.out.println("Invalid");
            }
        }
    }
} 