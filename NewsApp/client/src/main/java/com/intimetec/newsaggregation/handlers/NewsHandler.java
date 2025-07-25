package com.intimetec.newsaggregation.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.intimetec.newsaggregation.client.NewsClient;
import com.intimetec.newsaggregation.client.UserArticleClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;


public class NewsHandler {


    private final NewsClient newsClient;
    private final UserArticleClient userArticleClient;
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter;

    public NewsHandler(NewsClient newsClient, UserArticleClient userArticleClient, Scanner scanner) {
        this.newsClient = newsClient;
        this.userArticleClient = userArticleClient;
        this.scanner = scanner;
        this.dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    }

    public void displayTodayHeadlines() {
        try {
            List<JsonNode> headlines = newsClient.fetchToday();
            displayArticles(headlines, "TODAY'S HEADLINES");
        } catch (Exception exception) {
            System.out.println("Error fetching headlines: " + exception.getMessage());
        }
    }

    public void displayHeadlinesByDate(LocalDate date) {
        try {
            List<JsonNode> headlines = newsClient.fetchByDateRange(date, date);
            displayArticles(headlines, "HEADLINES FOR " + date.format(dateFormatter));
        } catch (Exception exception) {
            System.out.println("Error fetching headlines: " + exception.getMessage());
        }
    }

    public void displayHeadlinesByRange(LocalDate fromDate, LocalDate toDate) {
        try {
            List<JsonNode> headlines = newsClient.fetchByDateRange(fromDate, toDate);
            displayArticles(headlines, "HEADLINES FROM " + fromDate.format(dateFormatter) + " TO " + toDate.format(dateFormatter));
        } catch (Exception exception) {
            System.out.println("Error fetching headlines: " + exception.getMessage());
        }
    }


    public void searchArticles() {
        try {
            String query = getSearchQuery();
            if (query.isEmpty()) {
                System.out.println("Search query cannot be empty.");
                return;
            }

            LocalDate fromDate = null, toDate = null;
            if (shouldAddDateRange()) {
                fromDate = getDateInput("From (dd-MMM-yyyy): ");
                toDate = getDateInput("To (dd-MMM-yyyy): ");
            }

            List<JsonNode> results = (fromDate == null)
                    ? newsClient.search(query)
                    : newsClient.search(query, fromDate, toDate);

            if (results.isEmpty()) {
                System.out.println("\nNo results found.\n");
                return;
            }

            displayArticles(results, "SEARCH RESULTS (" + results.size() + ")");

        } catch (Exception exception) {
            System.out.println("Error searching articles: " + exception.getMessage());
        }
    }

    public void displaySavedArticles() {
        try {
            List<JsonNode> savedArticles = userArticleClient.listSaved();

            if (savedArticles.isEmpty()) {
                System.out.println("\nNo saved articles found.\n");
                return;
            }

            displayArticles(savedArticles, "SAVED ARTICLES (" + savedArticles.size() + ")");

        } catch (Exception exception) {
            System.out.println("Error fetching saved articles: " + exception.getMessage());
        }
    }

    private void displayArticles(List<JsonNode> articles, String title) {
        while (true) {
            displayArticleList(articles, title);
            String choice = getArticleMenuChoice();
            if (!handleArticleMenuChoice(choice)) {
                return;
            }
        }
    }

    private void displayArticleList(List<JsonNode> articles, String title) {
        System.out.printf("%n%s%n", title);
        articles.forEach(article -> System.out.printf("Id:%d  %s%n   %s%n",
                article.path("id").asLong(),
                article.path("title").asText(),
                article.path("url").asText()));
    }


    private String getArticleMenuChoice() {
        System.out.print("""
                1. Back
                2. Save Article
                3. Like
                4. Dislike
                5. Report Article
                > """);
        return scanner.nextLine().trim();
    }


    private boolean handleArticleMenuChoice(String choice) {
        switch (choice) {
            case "1" -> {
                return false;
            }
            case "2" -> handleSaveArticle();
            case "3" -> handleLikeArticle();
            case "4" -> handleDislikeArticle();
            case "5" -> handleReportArticle();
            default -> System.out.println("Invalid choice");
        }
        return true;
    }

    private String getSearchQuery() {
        System.out.print("\nEnter search text: ");
        return scanner.nextLine().trim();
    }

    private boolean shouldAddDateRange() {
        System.out.print("Add date range? (y/N): ");
        return "y".equalsIgnoreCase(scanner.nextLine().trim());
    }

    private void handleSaveArticle() {
        try {
            System.out.print("Enter ID to save: ");
            long articleId = Long.parseLong(scanner.nextLine().trim());
            userArticleClient.save(articleId);
            System.out.println("Article saved successfully!");
        } catch (NumberFormatException exception) {
            System.out.println("Invalid article ID format.");
        } catch (Exception exception) {
            System.out.println("Failed to save article" + exception.getMessage());
        }
    }


    private void handleLikeArticle() {
        try {
            System.out.print("Enter ID to like: ");
            long articleId = Long.parseLong(scanner.nextLine().trim());
            userArticleClient.like(articleId);
            System.out.println("Article liked successfully!");
        } catch (NumberFormatException exception) {
            System.out.println("Invalid article ID format.");
        } catch (Exception exception) {
            System.out.println("Failed to like article: " + exception.getMessage());
        }
    }


    private void handleDislikeArticle() {
        try {
            System.out.print("Enter ID to dislike: ");
            long articleId = Long.parseLong(scanner.nextLine().trim());
            userArticleClient.dislike(articleId);
            System.out.println("Article disliked successfully!");
        } catch (NumberFormatException exception) {
            System.out.println("Invalid article ID format.");
        } catch (Exception exception) {
            System.out.println("Failed to dislike article: " + exception.getMessage());
        }
    }


    private void handleReportArticle() {
        try {
            System.out.print("Enter article ID to report: ");
            long articleId = Long.parseLong(scanner.nextLine().trim());

            System.out.print("Enter reason for reporting: ");
            String reason = scanner.nextLine().trim();

            userArticleClient.report(articleId, reason);
            System.out.println("Article reported successfully!");

        } catch (NumberFormatException exception) {
            System.out.println("Invalid article ID format.");
        } catch (Exception exception) {
            System.out.println("Failed to report article: " + exception.getMessage());
        }
    }

    public void showSavedArticlesMenu() {
        displaySavedArticles();
    }


    public void showHeadlinesMenu() {
        while (true) {
            displayHeadlinesMenu();
            String choice = scanner.nextLine().trim();
            if (!handleHeadlinesMenuChoice(choice)) {
                return;
            }
        }
    }

    private void displayHeadlinesMenu() {
        System.out.println("\nHeadlines Menu:");
        System.out.println("1. Today");
        System.out.println("2. Date range");
        System.out.println("3. Back");
        System.out.print("> ");
    }

    private boolean handleHeadlinesMenuChoice(String choice) {
        switch (choice) {
            case "1" -> showCategoryFilterMenu();
            case "2" -> showDateRangeMenu();
            case "3" -> {
                return false;
            }
            default -> System.out.println("Invalid choice");
        }
        return true;
    }


    private void showCategoryFilterMenu() {
        displayCategoryFilterMenu();
        String choice = scanner.nextLine().trim();
        handleCategoryFilterChoice(choice);
    }

    private void displayCategoryFilterMenu() {
        System.out.println("\nFilter by category:");
        System.out.println("1. All");
        System.out.println("2. Business");
        System.out.println("3. Entertainment");
        System.out.println("4. Sports");
        System.out.println("5. Technology");
        System.out.println("6. Back");
        System.out.print("> ");
    }


    private void handleCategoryFilterChoice(String choice) {
        switch (choice) {
            case "1" -> displayTodayHeadlines();
            case "2" -> displayTodayHeadlinesByCategory("Business");
            case "3" -> displayTodayHeadlinesByCategory("Entertainment");
            case "4" -> displayTodayHeadlinesByCategory("Sports");
            case "5" -> displayTodayHeadlinesByCategory("Technology");
            case "6" -> {
                return;
            }
            default -> System.out.println("Invalid choice");
        }
    }

    private void showDateRangeMenu() {
        try {
            System.out.print("Enter from date (dd-MMM-yyyy): ");
            LocalDate fromDate = getDateInput("From (dd-MMM-yyyy): ");
            System.out.print("Enter to date (dd-MMM-yyyy): ");
            LocalDate toDate = getDateInput("To (dd-MMM-yyyy): ");

            displayHeadlinesByRange(fromDate, toDate);
        } catch (Exception exception) {
            System.out.println("Error with date range: " + exception.getMessage());
        }
    }

    private void displayTodayHeadlinesByCategory(String category) {
        try {
            List<JsonNode> headlines = newsClient.fetchByDateAndCategory(LocalDate.now(), category);
            displayArticles(headlines, "TODAY'S HEADLINES - " + category.toUpperCase());
        } catch (Exception exception) {
            System.out.println("Error fetching headlines for category '" + category + "': " + exception.getMessage());
        }
    }

    public void showSearchMenu() {
        searchArticles();
    }

    private LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return LocalDate.parse(input, dateFormatter);
            } catch (Exception exception) {
                System.out.println("Invalid date format. Please use dd-MMM-yyyy (e.g., 06-Jul-2024)");
            }
        }
    }
} 