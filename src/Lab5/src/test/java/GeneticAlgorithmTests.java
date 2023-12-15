import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class GeneticAlgorithmTests {

    @Test
    public void testGetProbabilityFromUserWithValidInput() {
        String input = "0.75";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        GeneticAlgorithm algorithm = new GeneticAlgorithm();
        Scanner scanner = new Scanner(System.in);

        double result = algorithm.getProbabilityFromUser("Enter a probability: ", scanner,3);

        assertEquals(0.75, result, 0.0001);
    }

    @Test
    public void testGetProbabilityFromUserWithInvalidInput() {
        String input = "abc\n1.5\n-0.2\n0.01";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        GeneticAlgorithm algorithm = new GeneticAlgorithm();
        Scanner scanner = new Scanner(System.in);

        double result = algorithm.getProbabilityFromUser("Enter a probability: ", scanner, 4);
        assertEquals(0.01, result, 0.0001);
    }


    @Test
    public void testInitializePopulation() {
        int populationSize = 10;
        List<GeneticAlgorithm.Individual> population = GeneticAlgorithm.initializePopulation(populationSize, new java.util.Random());

        assertEquals(populationSize, population.size());
        for (GeneticAlgorithm.Individual individual : population) {
            assertEquals(5, individual.getItems().size());
        }
    }
    @Test
    public void testEvaluatePopulation() {
        GeneticAlgorithm.Individual individual1 = new GeneticAlgorithm.Individual();
        individual1.getItems().add(new GeneticAlgorithm.Individual.Item(2.0, 3.0, 5.0));
        individual1.setWeightLimit(10.0);
        individual1.setVolumeLimit(5.0);

        GeneticAlgorithm.Individual individual2 = new GeneticAlgorithm.Individual();
        individual2.getItems().add(new GeneticAlgorithm.Individual.Item(1.0, 2.0, 3.0));
        individual2.setWeightLimit(5.0);
        individual2.setVolumeLimit(3.0);

        List<GeneticAlgorithm.Individual> population = List.of(individual1, individual2);

        GeneticAlgorithm.evaluatePopulation(population);

        assertEquals(5.0, individual1.getFitness(), 0.0001);
        assertEquals(3.0, individual2.getFitness(), 0.0001);
    }
    @Test
    public void testSelectParent() {
        GeneticAlgorithm.Individual individual1 = new GeneticAlgorithm.Individual();
        individual1.setFitness(5.0);

        GeneticAlgorithm.Individual individual2 = new GeneticAlgorithm.Individual();
        individual2.setFitness(3.0);

        GeneticAlgorithm.Individual individual3 = new GeneticAlgorithm.Individual();
        individual3.setFitness(7.0);

        List<GeneticAlgorithm.Individual> population = List.of(individual1, individual2, individual3);

        GeneticAlgorithm.Individual selectedParent = GeneticAlgorithm.selectParent(population, new java.util.Random());

        assertEquals(individual3, selectedParent);
    }
    @Test
    public void testCrossover() {
        GeneticAlgorithm.Individual parent1 = new GeneticAlgorithm.Individual();
        parent1.getItems().add(new GeneticAlgorithm.Individual.Item(2.0, 3.0, 5.0));
        parent1.getItems().add(new GeneticAlgorithm.Individual.Item(1.0, 2.0, 3.0));
        parent1.setWeightLimit(10.0);
        parent1.setVolumeLimit(5.0);

        GeneticAlgorithm.Individual parent2 = new GeneticAlgorithm.Individual();
        parent2.getItems().add(new GeneticAlgorithm.Individual.Item(4.0, 5.0, 8.0));
        parent2.getItems().add(new GeneticAlgorithm.Individual.Item(3.0, 4.0, 6.0));
        parent2.setWeightLimit(15.0);
        parent2.setVolumeLimit(8.0);

        GeneticAlgorithm.Individual child = GeneticAlgorithm.crossover(parent1, parent2, new java.util.Random());

        assertTrue(child.getItems().containsAll(parent1.getItems()) || child.getItems().containsAll(parent2.getItems()));
    }

}
