//package com.intimetec.newsaggregation.app;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.intimetec.newsaggregation.client.AdminClient;
//import com.intimetec.newsaggregation.client.AdminClient.ApiSourceDto;
//import com.intimetec.newsaggregation.client.AuthClient;
//import com.intimetec.newsaggregation.client.NewsClient;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Scanner;
//
///**
// * Console UI for News Aggregator with admin capabilities.
// */
//public class ConsoleMenu {
//
//    private static final String BASE_URL = "http://localhost:8081"; // server port for admin APIs
//
//    private final Scanner sc = new Scanner(System.in);
//    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
//    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");
//
//    private final AuthClient auth;
//    private final NewsClient news;
//
//    private String jwt;      // JWT token after login
//
//    public ConsoleMenu() {
//        ObjectMapper mapper = new ObjectMapper();
//        this.auth = new AuthClient(mapper);
//        this.news = new NewsClient(mapper, auth);
//    }
//
//    /* ------------------ ENTRY ------------------ */
//    public void start() throws Exception {
//        while (true) {
//            System.out.println("\n1. Login\n2. Sign up\n3. Exit\n> ");
//            String choice = sc.nextLine().trim();
//            switch (choice) {
//                case "1" -> {
//                    if (handleLogin()) {
//                        if ("ADMIN".equalsIgnoreCase(auth.getRole())) {
//                            adminMenu();
//                        } else {
//                            userMenu();
//                        }
//                    }
//                }
//                case "2" -> handleSignup();
//                case "3" -> {
//                    return;
//                }
//                default -> System.out.println("Invalid choice");
//            }
//        }
//    }
//
//    /* ------------- AUTH ------------- */
//    private boolean handleLogin() throws Exception {
//        System.out.print("Email    : ");
//        String email = sc.nextLine().trim();
//        System.out.print("Password : ");
//        String pwd = sc.nextLine().trim();
//
//        boolean ok = auth.login(email, pwd);
//        if (!ok) {
//            System.out.println("Login failed");
//            return false;
//        }
//        this.jwt = auth.getJwtToken();
//        System.out.println("Login successful" + ("ADMIN".equalsIgnoreCase(auth.getRole()) ? " (ADMIN)" : ""));
//        return true;
//    }
//
//    private void handleSignup() throws Exception {
//        System.out.print("Email    : ");
//        String email = sc.nextLine().trim();
//        System.out.print("Password : ");
//        String pwd = sc.nextLine().trim();
//        boolean ok = auth.signup(email, pwd);
//        System.out.println(ok ? "Signup successful" : "Signup failed");
//    }
//
//    /* ------------- ADMIN MENU ------------- */
//    private void adminMenu() throws Exception {
//        AdminClient admin = new AdminClient(BASE_URL, jwt);
//        while (true) {
//            System.out.println("""
//                    \nAdmin Options
//                    1. View external servers & status
//                    2. View external server details
//                    3. Add / Edit external server
//                    4. Add new News Category
//                    5. Logout
//                    > """);
//            switch (sc.nextLine().trim()) {
//                case "1" -> {
//                    ApiSourceDto[] list = admin.listApiSources();
//                    System.out.println("\n--- External Servers ---");
//                    for (ApiSourceDto s : list) {
//                        System.out.printf("[%d] %-18s %s%n", s.getId(), s.getName(), s.getStatus());
//                    }
//                }
//                case "2" -> {
//                    System.out.print("Server id: ");
//                    int id = Integer.parseInt(sc.nextLine().trim());
//                    try {
//                        ApiSourceDto d = admin.getApiSource(id);
//                        if (d == null || d.getId() == null) {
//                            System.out.println("No server found with ID " + id);
//                        } else {
//                            System.out.printf("\nId: %d\nName: %s\nEndpoint: %s\nAPI Key: %s\nPolling: %d\nStatus: %s\n",
//                                    d.getId(), d.getName(), d.getEndpointUrl(), d.getApiKey(), d.getPollingFreq(), d.getStatus());
//                        }
//                    } catch (Exception ex) {
//                        System.out.println("Error fetching server details: " + ex.getMessage());
//                    }
//                }
//                case "3" -> {
//                    ApiSourceDto dto = new ApiSourceDto();
//                    System.out.print("Id (blank=new): ");
//                    String idTxt = sc.nextLine().trim();
//                    if (!idTxt.isBlank()) dto.setId(Integer.parseInt(idTxt));
//                    System.out.print("Name        : ");
//                    dto.setName(sc.nextLine().trim());
//                    System.out.print("Endpoint URL: ");
//                    dto.setEndpointUrl(sc.nextLine().trim());
//                    System.out.print("API Key     : ");
//                    dto.setApiKey(sc.nextLine().trim());
//                    System.out.print("Polling freq: ");
//                    String freq = sc.nextLine().trim();
//                    if (!freq.isBlank()) dto.setPollingFreq(Integer.parseInt(freq));
//                    System.out.println("Saved => " + admin.saveApiSource(dto));
//                }
//                case "4" -> {
//                    System.out.print("New category name: ");
//                    String name = sc.nextLine().trim();
//                    System.out.println("Category added: " + admin.addCategory(name));
//                }
//                case "5" -> {
//                    System.out.println("Logged out.");
//                    return;
//                }
//                default -> System.out.println("Invalid option");
//            }
//        }
//    }
//
//    /* ------------- USER MENU ------------- */
//    private void userMenu() throws Exception {
//        while (true) {
//            System.out.printf("%nWelcome! Date: %s  Time: %s%n",
//                    LocalDate.now().format(dateFmt),
//                    LocalTime.now().format(timeFmt));
//            System.out.println("1. Headlines\n 4.Notification\n 5. Logout\n> ");
//            String c = sc.nextLine().trim();
//            if ("1".equals(c)) {
//                headlinesMenu();
//            } else if ("4".equals(c)) {
//
//            } else if ("5".equals(c)) {
//                System.out.println("Logged out.");
//                return;
//            }
//            else {
//                System.out.println("Invalid");
//            }
//        }
//    }
//
//    /* ----------- Headlines helpers ----------- */
//    private void headlinesMenu() throws Exception {
//        while (true) {
//            System.out.println("\nHeadlines Menu:\n1. Today\n2. Date range\n3. Back\n> ");
//            String ch = sc.nextLine().trim();
//            if ("1".equals(ch)) {
//                displayByDate(LocalDate.now());
//            } else if ("2".equals(ch)) {
//                System.out.print("From (dd-MMM-yyyy): ");
//                LocalDate from = LocalDate.parse(sc.nextLine().trim(), dateFmt);
//                System.out.print("To   (dd-MMM-yyyy): ");
//                LocalDate to = LocalDate.parse(sc.nextLine().trim(), dateFmt);
//                displayByRange(from, to);
//            } else if ("3".equals(ch)) {
//                return;
//            } else {
//                System.out.println("Invalid");
//            }
//        }
//    }
//
//    private void displayByDate(LocalDate date) throws Exception {
//        List<JsonNode> list = news.fetchToday();
//        categoryFilter(date, list);
//    }
//
//    private void displayByRange(LocalDate from, LocalDate to) throws Exception {
//        List<JsonNode> list = news.fetchByDateRange(from, to);
//        categoryFilter(from, list);
//    }
//
//
//    private void categoryFilter(LocalDate date, List<JsonNode> articles) throws Exception {
//        while (true) {
//            System.out.println("""
//
//                    Filter by category:
//                    1. All
//                    2. Business
//                    3. Entertainment
//                    4. Sports
//                    5. Technology
//                    6. Back
//                    >\s""");
//            String choice = sc.nextLine().trim();
//            String cat;
//
//            switch (choice) {
//                case "1" -> cat = null;
//                case "2" -> cat = "business";
//                case "3" -> cat = "entertainment";
//                case "4" -> cat = "sports";
//                case "5" -> cat = "technology";
//                case "6" -> {
//                    return;
//                }
//                default -> {
//                    System.out.println("Invalid");
//                    continue;
//                }
//            }
//
//            List<JsonNode> filtered = (cat == null)
//                    ? articles
//                    : news.fetchByDateAndCategory(date, cat);
//
//            System.out.println("\n--- HEADLINES ---");
//            for (int i = 0; i < filtered.size(); i++) {
//                JsonNode a = filtered.get(i);
//                System.out.printf(
//                        "%2d. %s%n   %s%n   URL: %s%n%n",
//                        i + 1,
//                        a.path("title").asText(),
//                        a.path("description").asText(""),
//                        a.path("url").asText("")
//                );
//            }
//        }
//    }
//
//    public static void main(String[] args) throws Exception {
//        new ConsoleMenu().start();
//    }
//}



package com.intimetec.newsaggregation.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intimetec.newsaggregation.client.AdminClient;
import com.intimetec.newsaggregation.client.AdminClient.ApiSourceDto;
import com.intimetec.newsaggregation.client.AuthClient;
import com.intimetec.newsaggregation.client.NewsClient;
import com.intimetec.newsaggregation.client.NotificationClient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Console UI for News Aggregator with admin & notification capabilities.
 * <p>
 * NOTE: Only the *notification* parts are new – everything that previously
 * existed remains functionally unchanged so your other menus still work.
 */
public class ConsoleMenu {

    private static final String BASE_URL = "http://localhost:8081"; // admin & user REST endpoints

    private final Scanner              sc       = new Scanner(System.in);
    private final DateTimeFormatter    dateFmt  = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private final DateTimeFormatter    timeFmt  = DateTimeFormatter.ofPattern("hh:mm a");

    private final AuthClient          auth;
    private final NewsClient          news;
    private final NotificationClient  notify;

    private String jwt;      // JWT token after login

    /* ------------------------------------------------------ */
    public ConsoleMenu() {
        ObjectMapper mapper = new ObjectMapper();
        this.auth   = new AuthClient(mapper);
        this.news   = new NewsClient(mapper, auth);
        this.notify = new NotificationClient(mapper, auth); //
    }

    /* ------------------ ENTRY ------------------ */
    public void start() throws Exception {
        while (true) {
            System.out.print("\n1. Login\n2. Sign up\n3. Exit\n> ");
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    if (handleLogin()) {
                        if ("ADMIN".equalsIgnoreCase(auth.getRole())) {
                            adminMenu();
                        } else {
                            userMenu();
                        }
                    }
                }
                case "2" -> handleSignup();
                case "3" -> {
                    return;
                }
                default -> System.out.println("Invalid choice");
            }
        }
    }

    /* ------------- AUTH ------------- */
    private boolean handleLogin() throws Exception {
        System.out.print("Email    : ");
        String email = sc.nextLine().trim();
        System.out.print("Password : ");
        String pwd = sc.nextLine().trim();

        boolean ok = auth.login(email, pwd);
        if (!ok) {
            System.out.println("Login failed");
            return false;
        }
        this.jwt = auth.getJwtToken();
        System.out.println("Login successful" + ("ADMIN".equalsIgnoreCase(auth.getRole()) ? " (ADMIN)" : ""));
        return true;
    }

    private void handleSignup() throws Exception {
        System.out.print("Email    : ");
        String email = sc.nextLine().trim();
        System.out.print("Password : ");
        String pwd = sc.nextLine().trim();
        boolean ok = auth.signup(email, pwd);
        System.out.println(ok ? "Signup successful" : "Signup failed");
    }

    /* ------------- ADMIN MENU (unchanged) ------------- */
    private void adminMenu() throws Exception {
        AdminClient admin = new AdminClient(BASE_URL, jwt);
        while (true) {
            System.out.println("""
                    \nAdmin Options
                    1. View external servers & status
                    2. View external server details
                    3. Add / Edit external server
                    4. Add new News Category
                    5. Logout
                    > """);
            switch (sc.nextLine().trim()) {
                case "1" -> {
                    ApiSourceDto[] list = admin.listApiSources();
                    System.out.println("\n--- External Servers ---");
                    for (ApiSourceDto s : list) {
                        System.out.printf("[%d] %-18s %s%n", s.getId(), s.getName(), s.getStatus());
                    }
                }
                case "2" -> {
                    System.out.print("Server id: ");
                    int id = Integer.parseInt(sc.nextLine().trim());
                    try {
                        ApiSourceDto d = admin.getApiSource(id);
                        if (d == null || d.getId() == null) {
                            System.out.println("No server found with ID " + id);
                        } else {
                            System.out.printf("\nId: %d\nName: %s\nEndpoint: %s\nAPI Key: %s\nPolling: %d\nStatus: %s\n",
                                    d.getId(), d.getName(), d.getEndpointUrl(), d.getApiKey(), d.getPollingFreq(), d.getStatus());
                        }
                    } catch (Exception ex) {
                        System.out.println("Error fetching server details: " + ex.getMessage());
                    }
                }
                case "3" -> {
                    ApiSourceDto dto = new ApiSourceDto();
                    System.out.print("Id (blank=new): ");
                    String idTxt = sc.nextLine().trim();
                    if (!idTxt.isBlank()) dto.setId(Integer.parseInt(idTxt));
                    System.out.print("Name        : ");
                    dto.setName(sc.nextLine().trim());
                    System.out.print("Endpoint URL: ");
                    dto.setEndpointUrl(sc.nextLine().trim());
                    System.out.print("API Key     : ");
                    dto.setApiKey(sc.nextLine().trim());
                    System.out.print("Polling freq: ");
                    String freq = sc.nextLine().trim();
                    if (!freq.isBlank()) dto.setPollingFreq(Integer.parseInt(freq));
                    System.out.println("Saved => " + admin.saveApiSource(dto));
                }
                case "4" -> {
                    System.out.print("New category name: ");
                    String name = sc.nextLine().trim();
                    System.out.println("Category added: " + admin.addCategory(name));
                }
                case "5" -> {
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------- USER MENU ------------- */
    private void userMenu() throws Exception {
        while (true) {
            System.out.printf("%nWelcome! Date: %s  Time: %s%n",
                    LocalDate.now().format(dateFmt),
                    LocalTime.now().format(timeFmt));
            System.out.print("1. Headlines\n4. Notifications\n5. Logout\n> ");
            String c = sc.nextLine().trim();
            if ("1".equals(c)) {
                headlinesMenu();
            } else if ("4".equals(c)) {
                notificationsMenu(); // ⭐ NEW ⭐
            } else if ("5".equals(c)) {
                System.out.println("Logged out.");
                return;
            } else {
                System.out.println("Invalid");
            }
        }
    }

    /* =================== NOTIFICATIONS =================== */
    private void notificationsMenu() throws Exception {
        while (true) {
            System.out.print("""
                    \nN O T I F I C A T I O N S
                    1. View Notifications
                    2. Configure Notifications
                    3. Back
                    4. Logout
                    > """);
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1" -> viewNotifications();
                case "2" -> configureNotifications();
                case "3" -> { return; }
                case "4" -> { System.out.println("Logged out."); System.exit(0); }
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ---- View ---- */
    private void viewNotifications() throws Exception {
        List<JsonNode> notes = notify.fetchNotifications();
        if (notes.isEmpty()) {
            System.out.println("\nNo notifications yet.\n");
            return;
        }
        System.out.println("\n--- Notifications ---");
        for (int i = 0; i < notes.size(); i++) {
            JsonNode n = notes.get(i);
            System.out.printf("%d. [%s] %s  (%s)\n   %s\n\n",
                    i + 1,
                    n.path("type").asText(""),
                    n.path("title").asText(""),
                    n.path("createdAt").asText("").replace('T', ' '),
                    n.path("url").asText(""));
        }
    }

    /* ---- Configure ---- */
    private void configureNotifications() throws Exception {
        JsonNode cfg = notify.fetchConfig();
        while (true) {
            System.out.println(buildConfigScreen(cfg));
            String opt = sc.nextLine().trim();
            switch (opt) {
                case "1", "2", "3", "4" -> {
                    toggleCategory(cfg, opt);
                    cfg = notify.saveConfig(cfg); // persist
                }
                case "5" -> { // keywords
                    cfg = manageKeywords(cfg);
                    cfg = notify.saveConfig(cfg);
                }
                case "6" -> { return; }
                case "7" -> { System.out.println("Logged out."); System.exit(0); }
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* helpers ----------------------------------------------------------- */
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
            default  -> "technology"; // "4"
        };
        boolean current = cfg.path(field).asBoolean();
        ((com.fasterxml.jackson.databind.node.ObjectNode) cfg).put(field, !current);
    }

    private JsonNode manageKeywords(JsonNode cfg) {
        List<String> kw = new ArrayList<>();
        cfg.path("keywords").forEach(k -> kw.add(k.path("term").asText()));

        while (true) {
            System.out.println("\nCurrent keywords: " + (kw.isEmpty() ? "<none>" : kw));
            System.out.print("1. Add  2. Remove  3. Done\n> ");
            String c = sc.nextLine().trim();
            if ("1".equals(c)) {
                System.out.print("Enter keyword: ");
                String k = sc.nextLine().trim().toLowerCase();
                if (!k.isBlank() && !kw.contains(k)) kw.add(k);
            } else if ("2".equals(c)) {
                System.out.print("Keyword to remove: ");
                kw.remove(sc.nextLine().trim().toLowerCase());
            } else if ("3".equals(c)) {
                break;
            } else {
                System.out.println("Invalid");
            }
        }

        // rewrite keywords array
        com.fasterxml.jackson.databind.node.ArrayNode arr = ((ObjectMapper) notify.getMapper()).createArrayNode();
        for (String k : kw) arr.add(k);
        ((com.fasterxml.jackson.databind.node.ObjectNode) cfg).set("keywords", arr);
        return cfg;
    }

    /* ----------- Headlines helpers (unchanged) ----------- */
    private void headlinesMenu() throws Exception {
        while (true) {
            System.out.print("\nHeadlines Menu:\n1. Today\n2. Date range\n3. Back\n> ");
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
            System.out.print("""
                    \nFilter by category:
                    1. All
                    2. Business
                    3. Entertainment
                    4. Sports
                    5. Technology
                    6. Back
                    > """);
            String choice = sc.nextLine().trim();
            String cat;

            switch (choice) {
                case "1" -> cat = null;
                case "2" -> cat = "business";
                case "3" -> cat = "entertainment";
                case "4" -> cat = "sports";
                case "5" -> cat = "technology";
                case "6" -> { return; }
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


