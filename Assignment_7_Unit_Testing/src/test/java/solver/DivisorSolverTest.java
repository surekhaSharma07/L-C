package solver;

import model.ProblemInput;
import service.DivisorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DivisorSolverTest {

    private DivisorSolver divisorSolver;

    @BeforeEach
    void setUp() {
        divisorSolver = new DivisorSolver(new DivisorService());
    }

    @Test
    void shouldReturnThreeMatches_whenInputIsFour() {
        ProblemInput input = new ProblemInput(4);
        int matches = divisorSolver.findMatchingPairs(input);
        assertEquals(3, matches, "Expected 3 matching pairs for input 4");
    }

    @Test
    void shouldReturnOneMatch_whenInputIsZero() {
        ProblemInput input = new ProblemInput(0);
        int matches = divisorSolver.findMatchingPairs(input);
        assertEquals(1, matches, "Expected 1 matching pair for input 0");
    }

    @Test
    void shouldReturnZeroMatches_whenInputIsNegative() {
        ProblemInput input = new ProblemInput(-5);
        int matches = divisorSolver.findMatchingPairs(input);
        assertEquals(0, matches, "Expected 0 matching pairs for negative input");
    }

    @Test
    void shouldReturnFourMatches_whenInputIsSix() {
        ProblemInput input = new ProblemInput(6);
        int matches = divisorSolver.findMatchingPairs(input);
        assertEquals(4, matches, "Expected 4 matching pairs for input 6");
    }

    @Test
    void shouldHandleLargeInputWithoutError() {
        ProblemInput input = new ProblemInput(100);
        int matches = divisorSolver.findMatchingPairs(input);
        assertTrue(matches >= 0, "Expected non-negative result for large input");
    }
}
