package com.intimetec.newsaggregation.handlers;

import com.intimetec.newsaggregation.client.AuthClient;
import com.intimetec.newsaggregation.exception.AuthenticationException;

import java.util.Scanner;

public class AuthenticationHandler {

    private final AuthClient authClient;
    private final Scanner scanner;

    public AuthenticationHandler(AuthClient authClient, Scanner scanner) {
        this.authClient = authClient;
        this.scanner = scanner;
    }

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

        } catch (AuthenticationException e) {
            System.out.println("Authentication error: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return false;
        }
    }


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

        } catch (AuthenticationException e) {
            System.out.println("Authentication error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Signup error: " + e.getMessage());
        }
    }


    private String getPassword() {
        return getPasswordSilent();
    }

    private String getPasswordSilent() {
        StringBuilder password = new StringBuilder();

        try {
            while (true) {
                int ch = System.in.read();

                if (ch == '\r' || ch == '\n') {
                    System.out.println();
                    break;
                } else if (ch == 8 || ch == 127) {
                    if (password.length() > 0) {
                        password.setLength(password.length() - 1);
                    }
                } else if (ch >= 32 && ch <= 126) {
                    password.append((char) ch);
                }
            }
        } catch (Exception e) {
            return getPasswordFallback();
        }

        return password.toString();
    }


    private String getPasswordFallback() {
        try {
            String input = scanner.nextLine();
            return input.trim();
        } catch (Exception e) {
            return "";
        }
    }

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

    private boolean isValidEmailFormat(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Simple but effective RFC 5322-compatible regex
        String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }

    public String getCurrentUserRole() {
        return authClient.getRole();
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(authClient.getRole());
    }
}