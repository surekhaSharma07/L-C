package com.intimetec.newsaggregation.handlers;

import com.intimetec.newsaggregation.client.AdminClient;
import com.intimetec.newsaggregation.dto.ApiSourceDto;
import com.intimetec.newsaggregation.dto.ReportDto;

import java.util.Scanner;

public class AdminHandler {

    private AdminClient adminClient;
    private final Scanner scanner;

    public AdminHandler(AdminClient adminClient, Scanner scanner) {
        this.adminClient = adminClient;
        this.scanner = scanner;
    }

    public void updateAdminClient(String jwtToken, String baseUrl) {
        if (jwtToken != null && !jwtToken.trim().isEmpty()) {
            this.adminClient = new AdminClient(baseUrl, jwtToken);
        } else {
            System.out.println("Warning: JWT token is null or empty. Please log in again.");
        }
    }

    public void displayArticleReports() {
        try {
            long articleId = getArticleIdFromUser();
            ReportDto[] reports = adminClient.fetchReports(articleId);

            if (reports.length == 0) {
                System.out.println("\nNo reports found for this article.\n");
                return;
            }

            displayReportsList(reports);

        } catch (NumberFormatException numberFormatException) {
            System.out.println("Invalid article ID format.");
        } catch (Exception exception) {
            System.out.println("Error fetching reports: " + exception.getMessage());
        }
    }

    private long getArticleIdFromUser() {
        System.out.print("Enter article ID to view reports: ");
        return Long.parseLong(scanner.nextLine().trim());
    }


    private void displayReportsList(ReportDto[] reports) {
        System.out.println("\nARTICLE REPORTS (" + reports.length + ")");
        for (ReportDto report : reports) {
            System.out.printf("User: %s%n", report.getUserEmail());
            System.out.printf("Reason: %s%n", report.getReason());
            System.out.printf("Reported At: %s%n", report.getReportedAt());
            System.out.println("---");
        }
    }

    public void viewExternalServers() {
        try {
            ApiSourceDto[] list = adminClient.listApiSources();
            System.out.println("\n--- External Servers ---");
            for (ApiSourceDto s : list)
                System.out.printf("[%d] %-18s %s%n",
                        s.getId(), s.getName(), s.getStatus());
        } catch (Exception exception) {
            System.out.println("Error: " + exception.getMessage());
        }
    }

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
        } catch (NumberFormatException exception) {
            System.out.println("Invalid server ID format.");
        } catch (Exception exception) {
            System.out.println("Error: " + exception.getMessage());
        }
    }

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
        } catch (Exception exception) {
            System.out.println("Error: " + exception.getMessage());
        }
    }

    public void addNewsCategory() {
        try {
            System.out.print("New category name: ");
            System.out.println("Category added: " +
                    adminClient.addCategory(scanner.nextLine().trim()));
        } catch (Exception exception) {
            System.out.println("Error: " + exception.getMessage());
        }
    }


    public void manageArticleModeration() {
        while (true) {
            printModerationMenu();
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> hideSingleArticle();
                case "2" -> unhideSingleArticle();
                case "3" -> hideAllInCategory();
                case "4" -> unhideAllInCategory();
                case "5" -> viewReportsForArticle();
                case "6" -> {
                    return;
                }
                default -> System.out.println("Invalid");
            }
        }
    }

    private void printModerationMenu() {
        System.out.print("""
                M O D E R A T E   A R T I C L E S
                1. Hide a single article
                2. Un‑hide a single article
                3. Hide all in a category
                4. Un‑hide all in a category
                5. View all reports for one article
                6. Back
                > """);
    }

    private void hideSingleArticle() {
        System.out.print("Article‑ID to hide : ");
        try {
            long id = Long.parseLong(scanner.nextLine().trim());
            adminClient.hideArticle(id);
            System.out.println("✔ hidden");
        } catch (Exception exception) {
          printError(exception);
        }
    }

    private void unhideSingleArticle() {
        System.out.print("Article‑ID to unhide: ");
        try {
            long id = Long.parseLong(scanner.nextLine().trim());
            adminClient.unhideArticle(id);
            System.out.println("✔ visible again");
        } catch (Exception exception) {
          printError(exception);
        }
    }

    private void hideAllInCategory() {
        System.out.print("Category‑ID to hide : ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            adminClient.hideCategory(id);
            System.out.println("✔ all hidden");
        } catch (Exception exception) {
          printError(exception);
        }
    }

    private void unhideAllInCategory() {
        System.out.print("Category‑ID to unhide: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            adminClient.unhideCategory(id);
            System.out.println("✔ all visible");
        } catch (Exception exception) {
          printError(exception);
        }
    }

    private void viewReportsForArticle() {
        System.out.print("Article‑ID : ");
        try {
            long id = Long.parseLong(scanner.nextLine().trim());
            ReportDto[] reports = adminClient.fetchReports(id);
            displayReports(reports);
        } catch (Exception exception) {
          printError(exception);
        }
    }

    private void displayReports(ReportDto[] reports) {
        if (reports == null || reports.length == 0) {
            System.out.println("\n(no reports)\n");
            return;
        }

        System.out.println("\n--- REPORTS ---");
        for (ReportDto r : reports) {
            String reportedAt = r.getReportedAt() == null ? "‑" : r.getReportedAt().replace('T', ' ');
            System.out.printf("- %s  (%s)  – \"%s\"%n",
                    r.getUserEmail(), reportedAt, r.getReason());
        }
    }

    private void printError(Exception exception) {
        System.out.println("Error: " + exception.getMessage());
    }
} 