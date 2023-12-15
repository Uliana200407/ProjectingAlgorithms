import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class KnapsackGeneticAlgorithm {
    private int populationSize;
    private int[][] population;
    private int[] values;
    private int[] weights;
    protected int capacity;
    private int numItems;
    private Random rand;

    public KnapsackGeneticAlgorithm(int populationSize, int capacity, int[] values, int[] weights) {
        this.populationSize = populationSize;
        this.capacity = capacity;
        this.values = values;
        this.weights = weights;
        this.numItems = values.length;
        this.rand = new Random();
        initializePopulation();
    }

    protected void initializePopulation() {
        population = new int[populationSize][numItems];
        for (int i = 0; i < populationSize; i++) {
            int itemIndex = rand.nextInt(numItems);
            population[i][itemIndex] = 1;
        }
    }

    protected int fitness(int[] individual) {
        int totalValue = 0;
        int totalWeight = 0;
        for (int i = 0; i < numItems; i++) {
            if (individual[i] == 1) {
                totalValue += values[i];
                totalWeight += weights[i];
            }
        }
        return (totalWeight <= capacity) ? totalValue : 0;
    }

    private int rouletteWheelSelection(int[][] currentPopulation) {
        int totalFitness = 0;
        for (int i = 0; i < populationSize; i++) {
            totalFitness += fitness(currentPopulation[i]);
        }

        double rouletteWheelPosition = rand.nextDouble() * totalFitness;
        double currentFitness = 0.0;

        for (int i = 0; i < populationSize; i++) {
            currentFitness += fitness(currentPopulation[i]);
            if (currentFitness >= rouletteWheelPosition) {
                return i;
            }
        }

        return populationSize - 1;
    }

    protected int[] onePointCrossover(int[] parent1, int[] parent2) {
        int[] offspring = new int[numItems];
        int crossoverPoint = rand.nextInt(numItems);

        for (int i = 0; i < crossoverPoint; i++) {
            offspring[i] = parent1[i];
        }
        for (int i = crossoverPoint; i < numItems; i++) {
            offspring[i] = parent2[i];
        }

        return offspring;
    }

    protected void mutate(int[] individual) {
        for (int i = 0; i < numItems; i++) {
            if (rand.nextDouble() < 0.05) {
                individual[i] = 1 - individual[i];
            }
        }
    }

    protected void localImprovement(int[] individual) {
        for (int i = 0; i < numItems; i++) {
            if (individual[i] == 1) {
                for (int j = 0; j < numItems; j++) {
                    if (individual[j] == 0 && weights[i] + calculateTotalWeight(individual) - weights[j] <= capacity) {
                        individual[i] = 0;
                        individual[j] = 1;
                        break;
                    }
                }
            }
        }
    }

    public int[] runGeneticAlgorithm(int maxIterations) {
        int[] bestSolution = null;
        int bestFitness = 0;

        try (FileWriter writer = new FileWriter("results.csv")) {
            writer.write("Iteration,BestFitness\n");

            for (int iteration = 0; iteration < maxIterations; iteration++) {
                int[][] selectedParents = new int[populationSize][numItems];
                for (int i = 0; i < populationSize; i++) {
                    int parent1Index = rouletteWheelSelection(population);
                    int parent2Index = rouletteWheelSelection(population);
                    selectedParents[i] = onePointCrossover(population[parent1Index], population[parent2Index]);
                    mutate(selectedParents[i]);
                    localImprovement(selectedParents[i]);
                }

                population = selectedParents;

                for (int i = 0; i < populationSize; i++) {
                    int currentFitness = fitness(population[i]);
                    if (currentFitness > bestFitness) {
                        bestFitness = currentFitness;
                        bestSolution = population[i].clone();
                    }
                }

                if (iteration % 20 == 0) {
                    writer.write(iteration + "," + bestFitness + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bestSolution;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int numItems = getNumberOfItems(scanner);
        int[] values = getValues(numItems, scanner);
        int[] weights = getWeights(numItems, scanner);

        int populationSize = 100;
        int capacity = 250;
        int maxIterations = 1000;

        KnapsackGeneticAlgorithm knapsackGA = new KnapsackGeneticAlgorithm(populationSize, capacity, values, weights);
        int[] bestSolution = knapsackGA.runGeneticAlgorithm(maxIterations);

        int bestFitness = knapsackGA.fitness(bestSolution);

        System.out.println("Best Solution:");
        System.out.println("Items selected:");
        for (int i = 0; i < numItems; i++) {
            if (bestSolution[i] == 1) {
                System.out.println("Item " + i + ": Value = " + values[i] + ", Weight = " + weights[i]);
            }
        }
        System.out.println("Total Value: " + bestFitness);
        System.out.println("Total Weight: " + knapsackGA.calculateTotalWeight(bestSolution));

        scanner.close();
    }

    private static int getNumberOfItems(Scanner scanner) {
        int numItems = 0;
        boolean isValidInput = false;

        while (!isValidInput) {
            System.out.print("Enter the number of items: ");
            String input = scanner.nextLine().trim();

            try {
                numItems = Integer.parseInt(input);
                if (numItems > 0 && !input.startsWith("0")) {
                    isValidInput = true;
                } else {
                    System.out.println("Invalid input. Please enter a positive integer without leading zeros.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }

        return numItems;
    }

    private static int[] getValues(int numItems, Scanner scanner) {
        int[] values = new int[numItems];

        for (int i = 0; i < numItems; i++) {
            System.out.print("Enter value for item " + (i + 1) + ": ");
            values[i] = readIntegerInput(scanner);
        }

        return values;
    }

    private static int[] getWeights(int numItems, Scanner scanner) {
        int[] weights = new int[numItems];

        for (int i = 0; i < numItems; i++) {
            System.out.print("Enter weight for item " + (i + 1) + ": ");
            weights[i] = readIntegerInput(scanner);
        }

        return weights;
    }

    private static int readIntegerInput(Scanner scanner) {
        int inputValue = 0;
        boolean isValidInput = false;

        while (!isValidInput) {
            try {
                String input = scanner.nextLine().trim();
                if (!input.startsWith("0")) {
                    inputValue = Integer.parseInt(input);
                    isValidInput = true;
                } else {
                    System.out.println("Invalid input. Please enter a valid integer without leading zeros.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }

        return inputValue;
    }

    public int calculateTotalWeight(int[] solution) {
        int totalWeight = 0;
        for (int i = 0; i < numItems; i++) {
            if (solution[i] == 1) {
                totalWeight += weights[i];
            }
        }
        return totalWeight;
    }

    public int[][] getPopulation() {
        return population;
    }

    public void setPopulation(int[][] population) {
        this.population = population;
    }
}
