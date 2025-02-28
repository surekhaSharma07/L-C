package com.commentsAssignment;

public class TumblrApiParser {

    public static void parseImageUrls(String jsonText, int start, int end) {
        String[] posts = jsonText.split("\"photos\":\\["); // Split JSON to isolate posts
        int postNumber = start;

        for (int i = 1; i < posts.length && postNumber <= end; i++) {
            String post = posts[i];

            if (!post.contains("\"photo-url-1280\"")) {
                continue;
            }

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

    public static String extractValue(String text, String startDelimiter, String endDelimiter) {
        int startIndex = text.indexOf(startDelimiter) + startDelimiter.length();
        int endIndex = text.indexOf(endDelimiter, startIndex);
        if (startIndex < startDelimiter.length() || endIndex < 0) return "Not Found";
        return text.substring(startIndex, endIndex);
    }

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

