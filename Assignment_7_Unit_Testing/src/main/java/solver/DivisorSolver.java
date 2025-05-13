package solver;

import model.ProblemInput;
import service.DivisorService;

public class DivisorSolver {

    private final DivisorService divisorService;

    public DivisorSolver(DivisorService divisorService) {
        this.divisorService = divisorService;
    }

    public int findMatchingPairs(ProblemInput input) {
        int inputNumber = input.getNumber();
        if (inputNumber < 0) {
            return 0;
        }
        int matches = 0;
        for (int i = 0; i <= inputNumber; i++) {
            if (divisorService.countPositiveDivisors(i) == divisorService.countPositiveDivisors(inputNumber - i)) {
                matches++;
            }
        }
        return matches;
    }
}