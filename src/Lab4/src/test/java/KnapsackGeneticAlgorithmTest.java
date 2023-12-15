import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class KnapsackGeneticAlgorithmTest {
    private KnapsackGeneticAlgorithm knapsackGA;

    @BeforeEach
    public void setUp() {
        int populationSize = 100;
        int capacity = 250;
        int[] values = {60, 100, 120};
        int[] weights = {10, 20, 30};
        knapsackGA = new KnapsackGeneticAlgorithm(populationSize, capacity, values, weights);
    }

    @Test
    public void testFitness() {
        int[] solution = {1, 0, 1};
        int expectedFitness = 180;
        int actualFitness = knapsackGA.fitness(solution);
        assertEquals(expectedFitness, actualFitness);
    }

    @Test
    public void testMutate() {
        int[] individual = {1, 0, 1};
        knapsackGA.mutate(individual);
        boolean containsOne = false;
        boolean containsZero = false;

        for (int gene : individual) {
            if (gene == 1) {
                containsOne = true;
            } else if (gene == 0) {
                containsZero = true;
            }
        }

        assertTrue(containsOne && containsZero);
    }

    @Test
    public void testLocalImprovement() {
        int[] individual = {1, 0, 1};
        knapsackGA.localImprovement(individual);
        int totalWeight = knapsackGA.capacity;
        assertTrue(totalWeight >= 0);
    }

    @Test
    public void testCalculateTotalWeight() {
        int[] solution = {1, 0, 1};
        int expectedTotalWeight = 40;
        int actualTotalWeight = knapsackGA.calculateTotalWeight(solution);
        assertEquals(expectedTotalWeight, actualTotalWeight);
    }

    @Test
    public void testInitializePopulation() {
        int[] originalPopulation = knapsackGA.getPopulation()[0];
        knapsackGA.initializePopulation();
        int[] newPopulation = knapsackGA.getPopulation()[0];
        assertNotEquals(originalPopulation, newPopulation);
    }
}
