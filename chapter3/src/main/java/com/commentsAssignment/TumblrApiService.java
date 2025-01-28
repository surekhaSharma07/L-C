package com.commentsAssignment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TumblrApiService {

    public static void fetchTumblrData(String blogName, int start, int end) {
        int apiStart = start - 1; // Convert start to zero-based indexing
        int numPosts = end - apiStart + 1;

        // Construct the API URL
        String apiUrl = "https://" + blogName + ".tumblr.com/api/read/json?type=photo&num=" + numPosts + "&start=" + apiStart;

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder jsonResponse = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonResponse.append(line);
            }
            reader.close();

            String jsonText = jsonResponse.toString()
                    .replace("var tumblr_api_read = ", "")
                    .replaceAll(";$", ""); // Clean the JSON response

            // Parse and display the blog information and image URLs
            TumblrApiParser.parseBlogInfo(jsonText);
            TumblrApiParser.parseImageUrls(jsonText, start, end);

        } catch (Exception e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }
    }
}
