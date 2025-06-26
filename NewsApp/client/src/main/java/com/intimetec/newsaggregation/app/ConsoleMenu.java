package com.intimetec.newsaggregation.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intimetec.newsaggregation.client.AuthClient;
import com.intimetec.newsaggregation.client.NewsClient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {
    private final Scanner sc = new Scanner(System.in);
    private final AuthClient auth;
    private final NewsClient news;
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");

    public ConsoleMenu() {
        ObjectMapper mapper = new ObjectMapper();
        this.auth = new AuthClient(mapper);
        this.news = new NewsClient(mapper, auth);
    }

    public void start() throws Exception {
        while (true) {
            System.out.println("""
                1. Login
                2. Sign up
                3. Exit
                >\s""");
            String choice = sc.nextLine().trim();
            if ("1".equals(choice) && handleLogin()) break;
            if ("2".equals(choice)) handleSignup();
            if ("3".equals(choice)) return;
            if (!"1".equals(choice) && !"2".equals(choice)) {
                System.out.println("Invalid choice");
            }
        }

        // — Main menu —
        while (true) {
            System.out.printf("%nWelcome to the News Application! Date: %s  Time: %s%n",
                    LocalDate.now().format(dateFmt),
                    LocalTime.now().format(timeFmt)
            );
            System.out.println("""
                1. Headlines
                2. Logout
                >\s""");
            String c = sc.nextLine().trim();
            if ("1".equals(c)) {
                headlinesMenu();
            } else if ("2".equals(c)) {
                System.out.println("Logged out.");
                return;
            } else {
                System.out.println("Invalid");
            }
        }
    }

    private boolean handleLogin() throws Exception {
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Password: ");
        String pwd = sc.nextLine().trim();
        boolean ok = auth.login(email, pwd);
        System.out.println(ok ? "Login successful" : "Login failed");
        return ok;
    }

    private void handleSignup() throws Exception {
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Password: ");
        String pwd = sc.nextLine().trim();
        boolean ok = auth.signup(email, pwd);
        System.out.println(ok ? "Signup successful" : "Signup failed");
    }

    private void headlinesMenu() throws Exception {
        while (true) {
            System.out.println("""
                
                Headlines Menu:
                1. Today
                2. Date range
                3. Back
                >\s""");
            String ch = sc.nextLine().trim();
            if ("1".equals(ch)) {
                displayByDate(LocalDate.now());
            } else if ("2".equals(ch)) {
                System.out.print("From (dd-MMM-yyyy): ");
                LocalDate from = LocalDate.parse(sc.nextLine().trim(), dateFmt);
                System.out.print("To   (dd-MMM-yyyy): ");
                LocalDate to = LocalDate.parse(sc.nextLine().trim(), dateFmt);
                displayByRange(from, to);
            } else if ("3".equals(ch)) {
                return;
            } else {
                System.out.println("Invalid");
            }
        }
    }

    private void displayByDate(LocalDate date) throws Exception {
        List<JsonNode> list = news.fetchToday();
        categoryFilter(date, list);
    }

    private void displayByRange(LocalDate from, LocalDate to) throws Exception {
        List<JsonNode> list = news.fetchByDateRange(from, to);
        categoryFilter(from, list);
    }

    private void categoryFilter(LocalDate date, List<JsonNode> articles) throws Exception {
        while (true) {
            System.out.println("""
                
                Filter by category:
                1. All
                2. Business
                3. Entertainment
                4. Sports
                5. Technology
                6. Back
                >\s""");
            String choice = sc.nextLine().trim();
            String cat;

            switch (choice) {
                case "1" -> cat = null;
                case "2" -> cat = "business";
                case "3" -> cat = "entertainment";
                case "4" -> cat = "sports";
                case "5" -> cat = "technology";
                case "6" -> {
                    return;
                }
                default -> {
                    System.out.println("Invalid");
                    continue;
                }
            }

            List<JsonNode> filtered = (cat == null)
                    ? articles
                    : news.fetchByDateAndCategory(date, cat);

            System.out.println("\n--- HEADLINES ---");
            for (int i = 0; i < filtered.size(); i++) {
                JsonNode a = filtered.get(i);
                System.out.printf(
                        "%2d. %s%n   %s%n   URL: %s%n%n",
                        i + 1,
                        a.path("title").asText(),
                        a.path("description").asText(""),
                        a.path("url").asText("")
                );
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new ConsoleMenu().start();
    }
}
