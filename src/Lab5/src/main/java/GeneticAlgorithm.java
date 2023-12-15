import java.util.*;

        public class GeneticAlgorithm {

            public static void main(String[] args) {
                Scanner scanner = new Scanner(System.in);
                int populationSize = getPositiveIntegerFromUser("Enter the population size: ", scanner);
                int numGenerations = getPositiveIntegerFromUser("Enter the number of generations: ", scanner);
                double mutationRate = getProbabilityFromUser("Enter the mutation rate: ", scanner,10);
                double localImprovementRate = getProbabilityFromUser("Enter the local improvement rate: ", scanner,10);

                List<Individual> population = initializePopulation(populationSize, new Random());

                for (int generation = 0; generation < numGenerations; generation++) {
                    evaluatePopulation(population);
                    System.out.println("Generation: " + generation);

                    for (int i = 0; i < population.size(); i++) {
                        System.out.println("Individual " + i + " Fitness: " + population.get(i).getFitness());
                    }

                    List<Individual> offspring = new ArrayList<>();
                    while (offspring.size() < population.size()) {
                        Individual parent1 = selectParent(population, new Random());
                        Individual parent2 = selectParent(population, new Random());
                        Individual child = crossover(parent1, parent2, new Random());
                        if (new Random().nextDouble() < mutationRate) {
                            mutate(child, new Random());
                        }
                        if (new Random().nextDouble() < localImprovementRate) {
                            localImprovement(child, new Random());
                        }
                        offspring.add(child);
                    }
                    population = offspring;
                }

                scanner.close();
            }
            protected static int getPositiveIntegerFromUser(String message, Scanner scanner) {
                int value = 0;

                while (true) {
                    System.out.print(message);
                    String input = scanner.next();

                    try {
                        value = Integer.parseInt(input);

                        if (value > 0 && !input.matches("^0[0-9].*")) {
                            break;
                        } else {
                            System.out.println("Please enter a positive integer without leading zeros.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid positive integer.");
                    }
                }

                return value;
            }

            protected static double getProbabilityFromUser(String message, Scanner scanner, int maxAttempts) {
                double probability = 0;

                for (int attempt = 0; attempt < maxAttempts; attempt++) {
                    System.out.print(message);
                    String input = scanner.next();

                    try {
                        probability = Double.parseDouble(input);

                        if (probability >= 0 && probability <= 1 && !input.matches("^0[0-9].*")) {
                            return probability;
                        } else {
                            System.out.println("Please enter a number between 0 and 1 without leading zeros.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                    }
                }

                System.out.println("Maximum number of attempts reached. Exiting...");
                return probability;
            }

            protected static List<Individual> initializePopulation(int populationSize, Random random) {
        List<Individual> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            Individual individual = new Individual();

            for (int j = 0; j < 5; j++) {
                double weight = random.nextDouble() * 10;
                double volume = random.nextDouble() * 5;
                double value = random.nextDouble() * 15;
                Individual.Item item = new Individual.Item(weight, volume, value);
                individual.getItems().add(item);
            }

            individual.setWeightLimit(random.nextDouble() * 100);
            individual.setVolumeLimit(random.nextDouble() * 50);

            population.add(individual);
        }
        return population;
    }

    protected static void evaluatePopulation(List < Individual > population) {
        for (Individual individual : population) {
            individual.evaluateFitness();
        }
    }

    protected static Individual selectParent(List < Individual > population, Random random) {
        int tournamentSize = 3;
        List<Individual> tournament = new ArrayList<>();

        for (int i = 0; i < tournamentSize; i++) {
            Individual randomIndividual = population.get(random.nextInt(population.size()));
            tournament.add(randomIndividual);
        }

        return Collections.max(tournament, Comparator.comparingDouble(Individual::getFitness));
    }

    protected static Individual crossover(Individual parent1, Individual parent2, Random random) {
        List<Individual.Item> parent1Items = parent1.getItems();
        List<Individual.Item> parent2Items = parent2.getItems();

        if (parent1Items.isEmpty() || parent2Items.isEmpty()) {
            Individual child = new Individual();
            child.setWeightLimit(parent1.getWeightLimit());
            child.setVolumeLimit(parent1.getVolumeLimit());
            return child;
        }

        int crossoverPoint1 = random.nextInt(parent1Items.size());
        int crossoverPoint2 = random.nextInt(parent1Items.size());
        int start = Math.min(crossoverPoint1, crossoverPoint2);
        int end = Math.max(crossoverPoint1, crossoverPoint2);

        Individual child = new Individual();
        List<Individual.Item> childItems = new ArrayList<>();

        childItems.addAll(parent1Items.subList(start, end));

        for (Individual.Item item : parent2Items) {
            if (!childItems.contains(item)) {
                childItems.add(item);
            }
        }

        child.setItems(childItems);
        child.setWeightLimit(parent1.getWeightLimit());
        child.setVolumeLimit(parent1.getVolumeLimit());

        return child;
    }

    private static void mutate(Individual individual, Random random) {
        List<Individual.Item> items = individual.getItems();

        if (items.size() >= 2) {
            int mutationIndex1 = random.nextInt(items.size());
            int mutationIndex2 = random.nextInt(items.size());

            Collections.swap(items, mutationIndex1, mutationIndex2);
        }
    }

    private static void localImprovement(Individual individual, Random random) {

                if (random.nextBoolean()) {
            double adjustment = (random.nextDouble() - 0.5) * 5;
            double newWeightLimit = individual.getWeightLimit() + adjustment;
            individual.setWeightLimit(newWeightLimit);
        } else {
            double adjustment = (random.nextDouble() - 0.5) * 2;
            double newVolumeLimit = individual.getVolumeLimit() + adjustment;
            individual.setVolumeLimit(newVolumeLimit);
        }
    }

    protected static class Individual {
        private List<Item> items;
        private double weightLimit;
        private double volumeLimit;
        private double fitness;

        public Individual() {
            this.items = new ArrayList<>();
        }

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }

        public double getWeightLimit() {
            return weightLimit;
        }

        public void setWeightLimit(double weightLimit) {
            this.weightLimit = weightLimit;
        }

        public double getVolumeLimit() {
            return volumeLimit;
        }

        public void setVolumeLimit(double volumeLimit) {
            this.volumeLimit = volumeLimit;
        }

        public double getFitness() {
            return fitness;
        }

        public void setFitness(double fitness) {
            this.fitness = fitness;
        }


        public void evaluateFitness() {
            double totalWeight = 0;
            double totalVolume = 0;
            double totalValue = 0;

            if (items == null) {
                System.out.println("Error: items is null");
                return;
            }

            for (Item item : items) {
                if (item == null) {
                    System.out.println("Error: item is null");
                    continue;
                }

                totalWeight += item.getWeight();
                totalVolume += item.getVolume();
                totalValue += item.getValue();
            }

            double weightPenalty = Math.max(0, totalWeight - weightLimit);
            double volumePenalty = Math.max(0, totalVolume - volumeLimit);

            fitness = totalValue - (weightPenalty + volumePenalty);

            System.out.println("Fitness calculated: " + fitness);
        }

        protected static class Item {
            private double weight;
            private double volume;
            private double value;

            public Item(double weight, double volume, double value) {
                this.weight = weight;
                this.volume = volume;
                this.value = value;
            }

            public double getWeight() {
                return weight;
            }
            public double getVolume() {
                return volume;
            }
            public double getValue() {
                return value;
            }

        }
    }
}
