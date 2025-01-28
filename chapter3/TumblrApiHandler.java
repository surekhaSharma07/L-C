package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
            fetchTumblrData(blogName, start, end);

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    // Fetches data from the Tumblr API for the given blog and range of posts.
    public static void fetchTumblrData(String blogName, int start, int end) {
        // Convert start to zero-based indexing as required by the API
        int apiStart = start - 1;
        int numPosts = end - apiStart + 1;

        // Construct the API URL dynamically based on user inputs
        String apiUrl = "https://" + blogName + ".tumblr.com/api/read/json?type=photo&num=" + numPosts + "&start=" + apiStart;

        try {
            // Open an HTTP connection to the Tumblr API
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            // Read the response from the API
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder jsonResponse = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonResponse.append(line); // Append each line to build the JSON response
            }
            reader.close();

            // Clean the JSON response by removing unwanted prefixes/suffixes
            String jsonText = jsonResponse.toString()
                    .replace("var tumblr_api_read = ", "")
                    .replaceAll(";$", "");

            // Process and display the blog information and image URLs
            parseBlogInfo(jsonText);
            parseImageUrls(jsonText, start, end);

        } catch (Exception e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }
    }

    public static void parseImageUrls(String jsonText, int start, int end) {
        String[] posts = jsonText.split("\"photos\":\\["); // Split JSON to isolate posts
        int postNumber = start; // Keep track of the post number

        for (int i = 1; i < posts.length && postNumber <= end; i++) {
            String post = posts[i];

            // Skip posts without the highest quality photo URL
            if (!post.contains("\"photo-url-1280\"")) {
                continue;
            }

            // Print the post number and extract image URLs
            System.out.println("Post " + postNumber + ":");
            String[] images = post.split("\"photo-url-1280\":\"");

            for (int j = 1; j < images.length; j++) {
                String imageUrl = images[j].split("\"")[0]; // Extract the image URL
                imageUrl = imageUrl.replace("\\", ""); // Remove escape characters
                System.out.println("   " + imageUrl);
            }
            postNumber++;
        }
    }

    //Extracts a substring value between two delimiters.
    public static String extractValue(String text, String startDelimiter, String endDelimiter) {
        int startIndex = text.indexOf(startDelimiter) + startDelimiter.length();
        int endIndex = text.indexOf(endDelimiter, startIndex);
        if (startIndex < startDelimiter.length() || endIndex < 0) return "Not Found";
        return text.substring(startIndex, endIndex); // Extract and return the value
    }

    //Parses and displays information about the blog (e.g., title, name, description).
    public static void parseBlogInfo(String jsonText) {

        // Extract blog details from the JSON response
        String title = extractValue(jsonText, "\"title\":\"", "\",");
        String name = extractValue(jsonText, "\"name\":\"", "\",");
        String description = extractValue(jsonText, "\"description\":\"", "\",");
        String postsTotal = extractValue(jsonText, "\"posts-total\":", ",");

        System.out.println("| title: " + title + "|");
        System.out.println("| name: " + name + "|");
        System.out.println("| description: " + description + "|");
        System.out.println("| no of post: " + postsTotal + "|");
    }
}



