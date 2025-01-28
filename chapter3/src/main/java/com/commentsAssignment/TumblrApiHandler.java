package com.commentsAssignment;

import java.util.Scanner;

public class TumblrApiHandler {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter the Tumblr blog name: ");
            String blogName = scanner.nextLine();

            System.out.print("Enter the range (e.g., 1-5): ");
            String range = scanner.nextLine();

            int start = Integer.parseInt(range.split("-")[0].trim());
            int end = Integer.parseInt(range.split("-")[1].trim());

            TumblrApiService.fetchTumblrData(blogName, start, end); // Call the service method

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}



