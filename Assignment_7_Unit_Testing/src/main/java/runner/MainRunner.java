package runner;

import model.ProblemInput;
import service.DivisorService;
import solver.DivisorSolver;

import java.util.Scanner;

public class MainRunner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DivisorSolver divisorSolver = new DivisorSolver(new DivisorService());

        System.out.print("Enter the target number: ");
        int inputNumber = scanner.nextInt();

        ProblemInput problemInput = new ProblemInput(inputNumber);
        int matchingPairs = divisorSolver.findMatchingPairs(problemInput);

        System.out.println("Matching Pairs: " + matchingPairs);
    }
}

