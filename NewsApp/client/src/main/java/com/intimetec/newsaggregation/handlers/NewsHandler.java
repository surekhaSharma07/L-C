package com.intimetec.newsaggregation.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.intimetec.newsaggregation.client.NewsClient;
import com.intimetec.newsaggregation.client.UserArticleClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Handles news-related operations (headlines, search, saved articles).
 * Extracted from ConsoleMenu to follow Single Responsibility Principle.
 */
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
        } catch (Exception e) {
            System.out.println("Error fetching headlines: " + e.getMessage());
        }
    }
    
    /**
     * Displays headlines for a specific date.
     */
    public void displayHeadlinesByDate(LocalDate date) {
        try {
            List<JsonNode> headlines = newsClient.fetchByDateRange(date, date);
            displayArticles(headlines, "HEADLINES FOR " + date.format(dateFormatter));
        } catch (Exception e) {
            System.out.println("Error fetching headlines: " + e.getMessage());
        }
    }
    
    /**
     * Displays headlines for a date range.
     */
    public void displayHeadlinesByRange(LocalDate fromDate, LocalDate toDate) {
        try {
            List<JsonNode> headlines = newsClient.fetchByDateRange(fromDate, toDate);
            displayArticles(headlines, "HEADLINES FROM " + fromDate.format(dateFormatter) + " TO " + toDate.format(dateFormatter));
        } catch (Exception e) {
            System.out.println("Error fetching headlines: " + e.getMessage());
        }
    }
    
    /**
     * Searches for articles with optional date range.
     */
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
            
        } catch (Exception e) {
            System.out.println("Error searching articles: " + e.getMessage());
        }
    }
    
    /**
     * Displays saved articles.
     */
    public void displaySavedArticles() {
        try {
            List<JsonNode> savedArticles = userArticleClient.listSaved();
            
            if (savedArticles.isEmpty()) {
                System.out.println("\nNo saved articles found.\n");
                return;
            }
            
            displayArticles(savedArticles, "SAVED ARTICLES (" + savedArticles.size() + ")");
            
        } catch (Exception e) {
            System.out.println("Error fetching saved articles: " + e.getMessage());
        }
    }
    
    /**
     * Displays articles with user interaction options.
     */
    private void displayArticles(List<JsonNode> articles, String title) {
        while (true) {
            displayArticleList(articles, title);
            String choice = getArticleMenuChoice();
            if (!handleArticleMenuChoice(choice)) {
                return;
            }
        }
    }
    
    /**
     * Displays the list of articles with their details.
     */
    private void displayArticleList(List<JsonNode> articles, String title) {
        System.out.printf("%n%s%n", title);
        articles.forEach(article -> System.out.printf("Id:%d  %s%n   %s%n",
                article.path("id").asLong(),
                article.path("title").asText(),
                article.path("url").asText()));
    }
    
    /**
     * Displays the article interaction menu and gets user choice.
     */
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
    
    /**
     * Handles the article menu choice selection.
     * 
     * @param choice the user's menu choice
     * @return true if should continue, false if should exit
     */
    private boolean handleArticleMenuChoice(String choice) {
        switch (choice) {
            case "1" -> { return false; }
            case "2" -> handleSaveArticle();
            case "3" -> handleLikeArticle();
            case "4" -> handleDislikeArticle();
            case "5" -> handleReportArticle();
            default -> System.out.println("Invalid choice");
        }
        return true;
    }
    
    /**
     * Gets search query from user.
     */
    private String getSearchQuery() {
        System.out.print("\nEnter search text: ");
        return scanner.nextLine().trim();
    }
    
    /**
     * Asks user if they want to add date range to search.
     */
    private boolean shouldAddDateRange() {
        System.out.print("Add date range? (y/N): ");
        return "y".equalsIgnoreCase(scanner.nextLine().trim());
    }
    
    /**
     * Handles saving an article.
     */
    private void handleSaveArticle() {
        try {
            System.out.print("Enter ID to save: ");
            long articleId = Long.parseLong(scanner.nextLine().trim());
            userArticleClient.save(articleId);
            System.out.println("Article saved successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid article ID format.");
        } catch (Exception e) {
            System.out.println("Failed to save article: " + e.getMessage());
        }
    }
    
    /**
     * Handles liking an article.
     */
    private void handleLikeArticle() {
        try {
            System.out.print("Enter ID to like: ");
            long articleId = Long.parseLong(scanner.nextLine().trim());
            userArticleClient.like(articleId);
            System.out.println("Article liked successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid article ID format.");
        } catch (Exception e) {
            System.out.println("Failed to like article: " + e.getMessage());
        }
    }
    
    /**
     * Handles disliking an article.
     */
    private void handleDislikeArticle() {
        try {
            System.out.print("Enter ID to dislike: ");
            long articleId = Long.parseLong(scanner.nextLine().trim());
            userArticleClient.dislike(articleId);
            System.out.println("Article disliked successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid article ID format.");
        } catch (Exception e) {
            System.out.println("Failed to dislike article: " + e.getMessage());
        }
    }
    
    /**
     * Handles reporting an article.
     */
    private void handleReportArticle() {
        try {
            System.out.print("Enter article ID to report: ");
            long articleId = Long.parseLong(scanner.nextLine().trim());
            
            System.out.print("Enter reason for reporting: ");
            String reason = scanner.nextLine().trim();
            
            if (reason.isEmpty()) {
                System.out.println("Reason cannot be empty. Please provide a reason for reporting.");
                return;
            }
            
            userArticleClient.report(articleId, reason);
            System.out.println("Article reported successfully!");
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid article ID format.");
        } catch (Exception e) {
            System.out.println("Failed to report article: " + e.getMessage());
        }
    }
    
    /**
     * Shows the saved articles menu.
     */
    public void showSavedArticlesMenu() {
        displaySavedArticles();
    }
    
    /**
     * Shows the headlines menu with options for Today, Date range, and category filtering.
     */
    public void showHeadlinesMenu() {
        while (true) {
            displayHeadlinesMenu();
            String choice = scanner.nextLine().trim();
            if (!handleHeadlinesMenuChoice(choice)) {
                return;
            }
        }
    }
    
    /**
     * Displays the headlines menu options.
     */
    private void displayHeadlinesMenu() {
        System.out.println("\nHeadlines Menu:");
        System.out.println("1. Today");
        System.out.println("2. Date range");
        System.out.println("3. Back");
        System.out.print("> ");
    }
    
    /**
     * Handles headlines menu choice selection.
     */
    private boolean handleHeadlinesMenuChoice(String choice) {
        switch (choice) {
            case "1" -> showCategoryFilterMenu();
            case "2" -> showDateRangeMenu();
            case "3" -> { return false; }
            default -> System.out.println("Invalid choice");
        }
        return true;
    }
    
    /**
     * Shows the category filter menu.
     */
    private void showCategoryFilterMenu() {
        displayCategoryFilterMenu();
        String choice = scanner.nextLine().trim();
        handleCategoryFilterChoice(choice);
    }
    
    /**
     * Displays the category filter menu options.
     */
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
    
    /**
     * Handles category filter choice selection.
     */
    private void handleCategoryFilterChoice(String choice) {
        switch (choice) {
            case "1" -> displayTodayHeadlines();
            case "2" -> displayTodayHeadlinesByCategory("Business");
            case "3" -> displayTodayHeadlinesByCategory("Entertainment");
            case "4" -> displayTodayHeadlinesByCategory("Sports");
            case "5" -> displayTodayHeadlinesByCategory("Technology");
            case "6" -> { return; }
            default -> System.out.println("Invalid choice");
        }
    }
    
    /**
     * Shows the date range menu.
     */
    private void showDateRangeMenu() {
        try {
            System.out.print("Enter from date (dd-MMM-yyyy): ");
            LocalDate fromDate = getDateInput("From (dd-MMM-yyyy): ");
            System.out.print("Enter to date (dd-MMM-yyyy): ");
            LocalDate toDate = getDateInput("To (dd-MMM-yyyy): ");
            
            displayHeadlinesByRange(fromDate, toDate);
        } catch (Exception e) {
            System.out.println("Error with date range: " + e.getMessage());
        }
    }
    
    /**
     * Displays today's headlines filtered by category.
     */
    private void displayTodayHeadlinesByCategory(String category) {
        try {
            List<JsonNode> headlines = newsClient.fetchByDateAndCategory(LocalDate.now(), category);
            displayArticles(headlines, "TODAY'S HEADLINES - " + category.toUpperCase());
        } catch (Exception e) {
            System.out.println("Error fetching headlines for category '" + category + "': " + e.getMessage());
        }
    }
    
    /**
     * Shows the search menu.
     */
    public void showSearchMenu() {
        searchArticles();
    }
    
    /**
     * Gets date input from user.
     */
    private LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return LocalDate.parse(input, dateFormatter);
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use dd-MMM-yyyy (e.g., 06-Jul-2024)");
            }
        }
    }
} 