package ui;

import java.util.Scanner;

public class ConsoleInputReader {
    private final Scanner scanner = new Scanner(System.in);

    public String readLocation() {
        System.out.print("Enter location/place: ");
        return scanner.nextLine().trim();
    }
}
