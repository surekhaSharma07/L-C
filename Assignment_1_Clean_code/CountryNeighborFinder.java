import java.util.Scanner;

public class CountryNeighborFinder {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Please enter a country code (e.g., IN, US, NZ): ");
        String countryCode = scanner.nextLine().toUpperCase();

        switch (countryCode) {
            case "IN":
                System.out.println("The neighboring countries of India (IN) are: Pakistan, China, Nepal, Bangladesh, and Bhutan.");
                break;
            case "US":
                System.out.println("The neighboring countries of the United States (US) are: Canada and Mexico.");
                break;
            case "NZ":
                System.out.println("New Zealand (NZ) does not share land borders, but its nearest neighbors are Australia, Fiji, and Tonga.");
                break;
            default:
                System.out.println("Information about neighboring countries for the code '" + countryCode + "' is not available.");
        }

        System.out.println("Thank you for using the Country Neighbor Finder application.");
    }
}