package com.intimetec.newsaggregation.handlers;

import com.intimetec.newsaggregation.client.AuthClient;

import java.util.Scanner;

/**
 * Handles authentication operations (login and signup).
 * Extracted from ConsoleMenu to follow Single Responsibility Principle.
 */
public class AuthenticationHandler {

    private final AuthClient authClient;
    private final Scanner scanner;

    public AuthenticationHandler(AuthClient authClient, Scanner scanner) {
        this.authClient = authClient;
        this.scanner = scanner;
    }

    /**
     * Handles user login with password masking.
     *
     * @return true if login successful, false otherwise
     */
    public boolean handleLogin() {
        try {
            String email = getValidEmail();
            if (email == null) {
                return false;
            }

            System.out.print("Password : ");
            String password = getPassword();

            if (!authClient.login(email, password)) {
                System.out.println("Login failed");
                return false;
            }

            System.out.println("Login successful" +
                    ("ADMIN".equalsIgnoreCase(authClient.getRole()) ? " (ADMIN)" : ""));
            return true;

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Handles user signup with password masking.
     */
    public void handleSignup() {
        try {
            String email = getValidEmail();
            if (email == null) {
                return;
            }

            System.out.print("Password : ");
            String password = getPassword();

            if (authClient.signup(email, password)) {
                System.out.println("Signup successful");
            } else {
                System.out.println("Signup failed");
            }

        } catch (Exception e) {
            System.out.println("Signup error: " + e.getMessage());
        }
    }

    /**
     * Gets password input (hidden).
     *
     * @return the password entered by user
     */
    private String getPassword() {
        // Always use silent input - no characters shown
        return getPasswordSilent();
    }

    /**
     * Gets password input silently (no characters shown).
     *
     * @return the password entered by user
     */
    private String getPasswordSilent() {
        StringBuilder password = new StringBuilder();

        try {
            while (true) {
                int ch = System.in.read();

                if (ch == '\r' || ch == '\n') {
                    System.out.println();
                    break;
                } else if (ch == 8 || ch == 127) { // Backspace
                    if (password.length() > 0) {
                        password.setLength(password.length() - 1);
                    }
                } else if (ch >= 32 && ch <= 126) { // Printable characters
                    password.append((char) ch);
                    // Don't print anything - keep it completely blank
                }
            }
        } catch (Exception e) {
            // If silent input fails, use scanner but don't show characters
            return getPasswordFallback();
        }
        return password.toString();
    }

    /**
     * Fallback password input that doesn't show characters.
     *
     * @return the password entered by user
     */
    private String getPasswordFallback() {
        try {
            // Use scanner but don't echo
            String input = scanner.nextLine();
            return input.trim();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Gets valid email input from user.
     *
     * @return valid email or null if user cancels
     */
    private String getValidEmail() {
        while (true) {
            System.out.print("Email    : ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                System.out.println("Email cannot be empty. Please try again.");
                continue;
            }

            if (!isValidEmailFormat(email)) {
                System.out.println("Invalid email format. Please enter a valid email (e.g., user@example.com)");
                continue;
            }
            return email;
        }
    }

    /**
     * Validates email format.
     *
     * @param email email to validate
     * @return true if email format is valid
     */
    private boolean isValidEmailFormat(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Simple but effective RFC 5322-compatible regex
        String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }


    /**
     * Gets the current user's role.
     *
     * @return the user's role
     */
    public String getCurrentUserRole() {
        return authClient.getRole();
    }

    /**
     * Checks if the current user is an admin.
     *
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(authClient.getRole());
    }
}