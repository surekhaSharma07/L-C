package com.intimetec.newsaggregation.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intimetec.newsaggregation.client.*;
import com.intimetec.newsaggregation.client.AdminClient.ApiSourceDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ConsoleMenu {


    private static final String BASE_URL = "http://localhost:8081";

    private final Scanner sc = new Scanner(System.in);
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");

    private final ObjectMapper mapper;
    private final AuthClient auth;
    private final NewsClient news;
    private final NotificationClient notify;
    private final UserArticleClient ua;           // NEW

    private String jwt; // current token – kept only if you need it elsewhere

    /* ------------------------------------------------------------------ */
    public ConsoleMenu() {
        this.mapper = new ObjectMapper();
        this.auth = new AuthClient(mapper);
        this.news = new NewsClient(mapper, auth);
        this.notify = new NotificationClient(mapper, auth);
        this.ua = new UserArticleClient(auth, mapper);   // NEW
    }

    /* ================================================================ */
    /* ENTRY                                                            */
    /* ================================================================ */
    public void start() throws Exception {
        while (true) {
            System.out.print("\n1. Login\n2. Sign up\n3. Exit\n> ");
            switch (sc.nextLine().trim()) {
                case "1" -> {
                    if (handleLogin()) {
                        if ("ADMIN".equalsIgnoreCase(auth.getRole())) adminMenu();
                        else userMenu();
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

    private boolean handleLogin() throws Exception {
        System.out.print("Email    : ");
        String email = sc.nextLine().trim();
        System.out.print("Password : ");
        String pwd = sc.nextLine().trim();

        if (!auth.login(email, pwd)) {
            System.out.println("Login failed");
            return false;
        }
        jwt = auth.getJwtToken();
        System.out.println("Login successful" +
                ("ADMIN".equalsIgnoreCase(auth.getRole()) ? " (ADMIN)" : ""));
        return true;
    }

    private void handleSignup() throws Exception {
        System.out.print("Email    : ");
        String email = sc.nextLine().trim();
        System.out.print("Password : ");
        String pwd = sc.nextLine().trim();

        System.out.println(auth.signup(email, pwd)
                ? "Signup successful" : "Signup failed");
    }

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
                    for (ApiSourceDto s : list)
                        System.out.printf("[%d] %-18s %s%n",
                                s.getId(), s.getName(), s.getStatus());
                }
                case "2" -> {
                    System.out.print("Server id: ");
                    int id = Integer.parseInt(sc.nextLine().trim());
                    try {
                        ApiSourceDto d = admin.getApiSource(id);
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
                    } catch (Exception ex) {
                        System.out.println("Error: " + ex.getMessage());
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
                    System.out.println("Category added: " +
                            admin.addCategory(sc.nextLine().trim()));
                }
                case "5" -> {
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* =================================== USER MENU ================== */
    private void userMenu() throws Exception {
        while (true) {
            System.out.printf("%nWelcome! Date: %s  Time: %s%n",
                    LocalDate.now().format(dateFmt),
                    LocalTime.now().format(timeFmt));

            System.out.print("""
                    1. Headlines
                    2. Saved Articles
                    3. Search
                    4. Notifications
                    5. Logout
                    > """);

            switch (sc.nextLine().trim()) {
                case "1" -> headlinesMenu();
                case "2" -> savedArticlesMenu();
                case "3" -> searchMenu();
                case "4" -> notificationsMenu();
                case "5" -> {
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid");
            }
        }
    }

    /* =================== NOTIFICATIONS (unchanged) ================== */
    private void notificationsMenu() throws Exception {
        while (true) {
            System.out.print("""
                    \nN O T I F I C A T I O N S
                    1. View Notifications
                    2. Configure Notifications
                    3. Back
                    4. Logout
                    > """);

            switch (sc.nextLine().trim()) {
                case "1" -> viewNotifications();
                case "2" -> configureNotifications();
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

    /* ---------- notify helpers (unchanged) ---------- */
    private void viewNotifications() throws Exception {
        List<JsonNode> notes = notify.fetchNotifications();
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
            default -> "technology";
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
            switch (sc.nextLine().trim()) {
                case "1" -> {
                    System.out.print("Enter keyword: ");
                    String k = sc.nextLine().trim().toLowerCase();
                    if (!k.isBlank() && !kw.contains(k)) kw.add(k);
                }
                case "2" -> {
                    System.out.print("Keyword to remove: ");
                    kw.remove(sc.nextLine().trim().toLowerCase());
                }
                case "3" -> {
                    /* rebuild the keywords JSON array exactly as the backend expects */
                    var arr = mapper.createArrayNode();
                    kw.forEach(k ->
                            arr.add(mapper.createObjectNode()
                                    .putNull("id")
                                    .put("term", k)));
                    ((com.fasterxml.jackson.databind.node.ObjectNode) cfg).set("keywords", arr);
                    return cfg;                         // finished editing
                }
                default -> System.out.println("Invalid");
            }
        }
    }


    /* ===================  SAVED‑ARTICLES  ======================= */
    private void savedArticlesMenu() {
        while (true) {
            var saved = ua.listSaved();

            if (saved.isEmpty()) System.out.println("\n< No saved articles >");
            else {
                System.out.println("\nS A V E D   A R T I C L E S");
                saved.forEach(a -> System.out.printf(
                        "Id:%d  %s%n   %s%n",
                        a.path("id").asLong(),
                        a.path("title").asText(),
                        a.path("url").asText()));
            }

            System.out.print("""
                    1. Back
                    2. Delete Article
                    3. Like Article
                    4. Dislike Article
                    > """);

            switch (sc.nextLine().trim()) {
                case "1" -> {
                    return;
                }
                case "2" -> {
                    System.out.print("Enter ID: ");
                    ua.unsave(Long.parseLong(sc.nextLine().trim()));
                }
                case "3" -> {
                    System.out.print("Enter ID: ");
                    ua.like(Long.parseLong(sc.nextLine().trim()));
                }
                case "4" -> {
                    System.out.print("Enter ID: ");
                    ua.dislike(Long.parseLong(sc.nextLine().trim()));
                }
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ===================  HEADLINES  ============================ */
    private void headlinesMenu() throws Exception {
        while (true) {
            System.out.print("""
                    \nHeadlines Menu:
                    1. Today
                    2. Date range
                    3. Back
                    > """);
            switch (sc.nextLine().trim()) {
                case "1" -> displayByDate(LocalDate.now());
                case "2" -> {
                    System.out.print("From (dd-MMM-yyyy): ");
                    LocalDate from = LocalDate.parse(sc.nextLine().trim(), dateFmt);
                    System.out.print("To   (dd-MMM-yyyy): ");
                    LocalDate to = LocalDate.parse(sc.nextLine().trim(), dateFmt);
                    displayByRange(from, to);
                }
                case "3" -> {
                    return;
                }
                default -> System.out.println("Invalid");
            }
        }
    }

    private void displayByDate(LocalDate d) throws Exception {
        categoryFilter(d, news.fetchToday());
    }

    private void displayByRange(LocalDate f, LocalDate t) throws Exception {
        categoryFilter(f, news.fetchByDateRange(f, t));
    }

    private void categoryFilter(LocalDate date, List<JsonNode> arts) throws Exception {
        while (true) {
            System.out.print("""
                     \nFilter by category:
                     1. All
                     2. Business
                     3. Entertainment
                     4.Sports
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
                case "6" -> {
                    return;
                }
                default -> {
                    System.out.println("Invalid");
                    continue;
                }
            }
            List<JsonNode> list = (cat == null)
                    ? arts
                    : news.fetchByDateAndCategory(date, cat);

            System.out.println("\n--- HEADLINES ---");
            list.forEach(a -> System.out.printf(
                    "Id:%d  %s%n   %s%n%n",
                    a.path("id").asLong(),
                    a.path("title").asText(),
                    a.path("url").asText()));

            /* ----- inline actions ----- */
            System.out.print("""
                    1. Back
                    2. Save Article
                    3. Like
                    4. Dislike
                    > """);

            switch (sc.nextLine().trim()) {
                case "1" -> {
                    return;
                }
                case "2" -> {
                    System.out.print("Enter ID to save: ");
                    ua.save(Long.parseLong(sc.nextLine().trim()));
                }
                case "3" -> {
                    System.out.print("Enter ID to like: ");
                    ua.like(Long.parseLong(sc.nextLine().trim()));
                }
                case "4" -> {
                    System.out.print("Enter ID to dislike: ");
                    ua.dislike(Long.parseLong(sc.nextLine().trim()));
                }
                default -> System.out.println("Invalid");
            }
        }
    }

    // add near the other fields – it reuses the same pattern
    private final DateTimeFormatter df = dateFmt;   // just an alias

    // -----------------------------------------------------------------
// add this inside ConsoleMenu (e.g. after savedArticlesMenu)
// -----------------------------------------------------------------
    /* ===================  SEARCH  ============================== */
    private void searchMenu() throws Exception {
        System.out.print("\nEnter search text: ");
        String query = sc.nextLine().trim();
        if (query.isBlank()) {
            System.out.println("Empty query.");
            return;
        }

        LocalDate from = null, to = null;
        System.out.print("Add date range? (y/N): ");
        if ("y".equalsIgnoreCase(sc.nextLine().trim())) {
            System.out.print("From (dd-MMM-yyyy): ");
            from = LocalDate.parse(sc.nextLine().trim(), dateFmt);
            System.out.print("To   (dd-MMM-yyyy): ");
            to = LocalDate.parse(sc.nextLine().trim(), dateFmt);
        }

        List<JsonNode> results = (from == null)
                ? news.search(query)
                : news.search(query, from, to);

        if (results.isEmpty()) {
            System.out.println("\nNo results.\n");
            return;
        }

        while (true) {
            System.out.printf("%nS E A R C H   R E S U L T S  (%d)%n", results.size());
            results.forEach(a -> System.out.printf("Id:%d  %s%n   %s%n",
                    a.path("id").asLong(),
                    a.path("title").asText(),
                    a.path("url").asText()));

            System.out.print("""
                    1. Back
                    2. Save
                    3. Like
                    4. Dislike
                    > """);
            switch (sc.nextLine().trim()) {
                case "1" -> {
                    return;
                }
                case "2" -> {
                    System.out.print("Id: ");
                    ua.save(Long.parseLong(sc.nextLine().trim()));
                }
                case "3" -> {
                    System.out.print("Id: ");
                    ua.like(Long.parseLong(sc.nextLine().trim()));
                }
                case "4" -> {
                    System.out.print("Id: ");
                    ua.dislike(Long.parseLong(sc.nextLine().trim()));
                }
                default -> System.out.println("Invalid");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new ConsoleMenu().start();
    }
}

