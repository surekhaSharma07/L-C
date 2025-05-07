package app;

import service.GeoService;
import ui.ConsoleInputReader;
import ui.ConsolePrinter;

public class GeoLocatorApp {
    public static void main(String[] args) {
        ConsoleInputReader inputReader = new ConsoleInputReader();
        ConsolePrinter printer = new ConsolePrinter();
        GeoService geoService = new GeoService(printer);

        String location = inputReader.readLocation();
        geoService.locatePlace(location);
    }
}
