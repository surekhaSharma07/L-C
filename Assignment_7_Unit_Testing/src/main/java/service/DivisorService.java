package service;

public class DivisorService {

    public int countPositiveDivisors(int number) {
        if (number == 0) {
            return 0;
        }

        int divisorCount = 0;
        int squareRootOfNumber = (int) Math.sqrt(number);

        for (int possibleDivisor = 1; possibleDivisor <= squareRootOfNumber; possibleDivisor++) {
            if (number % possibleDivisor == 0) {
                divisorCount++;
                if (possibleDivisor != number / possibleDivisor) {
                    divisorCount++;
                }
            }
        }

        return divisorCount;
    }
}

