package service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import util.HttpHelper;
import ui.Printer;

public class GeoService {
    private static final String API_KEY = "681b82c131fa6296275457wpnf56147";
    private static final String BASE_URL = "https://geocode.maps.co/search?q=";

    private final Printer printer;

    public GeoService(Printer printer) {
        this.printer = printer;
    }

    public void locatePlace(String place) {
        String url = BASE_URL + place.replace(" ", "+") + "&api_key=" + API_KEY;
        try {
            String response = HttpHelper.get(url);
            JsonArray results = JsonParser.parseString(response).getAsJsonArray();

            if (results.size() > 0) {
                JsonObject loc = results.get(0).getAsJsonObject();
                printer.print("Latitude: " + loc.get("lat").getAsString());
                printer.print("Longitude: " + loc.get("lon").getAsString());
            } else {
                printer.print("Location not found.");
            }
        } catch (Exception e) {
            printer.print("Error occurred while fetching coordinates: " + e.getMessage());
        }
    }
}
