import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class MazeSolver {
    private static final Random random = new Random();

    record Maze(boolean[][] grid) {

        public boolean isWall(int row, int col) {
            return grid[row][col];
        }

        public boolean[][] getGrid() {
            return grid;
        }
    }


    static class PathState {
        protected final boolean[][] path;
        private final int steps;

        public PathState(boolean[][] path, int steps) {
            this.path = path;
            this.steps = steps;
        }

        public int getRowCount() {
            return path.length;
        }

        public int getColCount() {
            return path[0].length;
        }

        public int getSteps() {
            return steps;
        }
    }

    public static Maze generateMaze(int size) {
        boolean[][] grid = new boolean[size][size];
        for (boolean[] row : grid) {
            Arrays.fill(row, true);
        }

        for (int i = 0; i < size; i += 2) {
            for (int j = 0; j < size; j += 2) {
                grid[i][j] = false;
            }
        }

        List<int[]> frontier = new ArrayList<>();
        int startX = random.nextInt(size);
        int startY = random.nextInt(size);
        frontier.add(new int[]{startX, startY});
        grid[startX][startY] = false;

        while (!frontier.isEmpty()) {
            int[] current = frontier.remove(random.nextInt(frontier.size()));
            int x = current[0];
            int y = current[1];

            List<int[]> neighbors = new ArrayList<>();

            if (x >= 2) {
                neighbors.add(new int[]{x - 2, y});
            }
            if (x < size - 2) {
                neighbors.add(new int[]{x + 2, y});
            }
            if (y >= 2) {
                neighbors.add(new int[]{x, y - 2});
            }
            if (y < size - 2) {
                neighbors.add(new int[]{x, y + 2});
            }

            for (int[] neighbor : neighbors) {
                int nx = neighbor[0];
                int ny = neighbor[1];

                if (grid[nx][ny]) {
                    grid[nx][ny] = false;
                    frontier.add(new int[]{nx, ny});
                }
            }
        }

        return new Maze(grid);
    }

    static int countDeadEnds(PathState result) {
        boolean[][] path = result.path;
        int deadEnds = 0;
        int rowCount = result.getRowCount();
        int colCount = result.getColCount();

        for (int i = 1; i < rowCount - 1; i++) {
            for (int j = 1; j < colCount - 1; j++) {
                if (!path[i][j]) {
                    int neighbors = countPathNeighbors(path, i, j);
                    if (neighbors < 2) {
                        deadEnds++;
                    }
                }
            }
        }
        return deadEnds;
    }

    static int countPathNeighbors(boolean[][] path, int i, int j) {
        int neighbors = 0;
        if (!path[i - 1][j]) neighbors++;
        if (!path[i + 1][j]) neighbors++;
        if (!path[i][j - 1]) neighbors++;
        if (!path[i][j + 1]) neighbors++;
        return neighbors;
    }

    static int countGeneratedStates(PathState result) {
        int generatedStates = 0;
        boolean[][] path = result.path;
        int rowCount = result.getRowCount();
        int colCount = result.getColCount();

        for (int i = 1; i < rowCount - 1; i++) {
            for (int j = 1; j < colCount - 1; j++) {
                if (!path[i][j]) {
                    generatedStates++;
                }
            }
        }
        return generatedStates;
    }

    static List<PathState> runExperiments(int numExperiments, int size, String fileName, boolean useAStar) {
        List<PathState> results = new ArrayList<>();

        for (int i = 0; i < numExperiments; i++) {
            Maze maze = generateMaze(size);
            int startX = random.nextInt(size);
            int startY = random.nextInt(size);
            PathState result = useAStar ? AStarSearch(maze, startX, startY) : LdfsSearch(maze, startX, startY);

            results.add(result);
            saveMazeToFile(maze, fileName + "_" + (i + 1) + ".txt", startX, startY, result.path);
        }

        return results;
    }

    static void deleteOldFiles(int numExperiments, String fileName) {
        for (int i = 1; i <= numExperiments; i++) {
            String filePath = fileName + "_" + i + ".txt";
            File file = new File(filePath);
            if (file.exists() && file.isFile() && file.delete()) {
                System.out.println("File " + filePath + " was deleted.");
            }
        }
    }

    static void saveMazeToFile(Maze maze, String fileName, int startX, int startY, boolean[][] path) {
        int rowCount = maze.grid.length;
        int colCount = maze.grid[0].length;

        try (FileWriter writer = new FileWriter(fileName)) {
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < colCount; col++) {
                    if (row == startX && col == startY) {
                        writer.write("S ");
                    } else if (row == rowCount - 2 && col == colCount - 2) {
                        writer.write("E ");
                    } else if (path[row][col]) {
                        writer.write(". ");
                    } else if (maze.isWall(row, col)) {
                        writer.write("██");
                    } else {
                        writer.write("  ");
                    }
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static PathState LdfsSearch(Maze maze, int startX, int startY) {
        int size = maze.grid.length;
        boolean[][] path = new boolean[size][size];
        int steps = 0;

        Stack<Position> stack = new Stack<>();
        stack.push(new Position(startX, startY));

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        while (!stack.isEmpty()) {
            Position current = stack.pop();
            int x = current.x;
            int y = current.y;

            if (x == size - 2 && y == size - 2) {
                steps = path.length * path[0].length - stack.size();
                break;
            }

            if (path[x][y]) {
                continue;
            }

            path[x][y] = true;

            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (newX >= 0 && newX < size && newY >= 0 && newY < size && !path[newX][newY]) {
                    stack.push(new Position(newX, newY));
                }
            }
        }

        return new PathState(path, steps);
    }

    static PathState AStarSearch(Maze maze, int startX, int startY) {
        int size = maze.grid.length;
        boolean[][] path = new boolean[size][size];
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        PriorityQueue<AStarState> openSet = new PriorityQueue<>(Comparator.comparingInt(AStarState::f));
        openSet.add(new AStarState(startX, startY, 0, h2(startX, startY, size - 2, size - 2), null));

        Set<AStarState> visitedNodes = new HashSet<>();
        visitedNodes.add(openSet.peek());

        while (!openSet.isEmpty()) {
            AStarState current = openSet.poll();
            visitedNodes.remove(current);
            int x = current.x;
            int y = current.y;

            if (x == size - 2 && y == size - 2) {
                reconstructPath(path, current);
                return new PathState(path, current.g);
            }

            if (path[x][y]) {
                continue;
            }

            path[x][y] = true;

            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (newX >= 0 && newX < size && newY >= 0 && newY < size && !path[newX][newY] && !maze.isWall(newX, newY)) {
                    int tentativeG = current.g + 1;
                    int h = h2(newX, newY, size - 2, size - 2);
                    AStarState neighbor = new AStarState(newX, newY, tentativeG, h, current);

                    if (!visitedNodes.contains(neighbor) || tentativeG < getGValue(openSet, neighbor)) {
                        openSet.add(neighbor);
                        visitedNodes.add(neighbor);
                    }
                }
            }
        }

        return new PathState(path, 0);
    }

    static int getGValue(PriorityQueue<AStarState> set, AStarState state) {
        return set.stream()
                .filter(s -> s.x == state.x && s.y == state.y)
                .findFirst()
                .map(s -> s.g)
                .orElse(state.g);
    }
    static int h2(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
    static void reconstructPath(boolean[][] path, AStarState goal) {
        while (goal != null) {
            int x = goal.x;
            int y = goal.y;
            path[x][y] = true;
            goal = goal.parent;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int sizeMB = getInputSize(scanner);
        int numExperiments = getInputNumExperiments(scanner);
        String fileName = getFileName(scanner);

        List<PathState> experimentResults = new ArrayList<>();

        int algorithmChoice;

        while (true) {
            System.out.println("Choose an algorithm to use:");
            System.out.println("1. A* with Manhattan Distance (Enter '1')");
            System.out.println("2. LDFS (Enter '2')");

            if (scanner.hasNext()) {
                String input = scanner.next();

                if (input.matches("[1-2]")) {
                    algorithmChoice = Integer.parseInt(input);
                    break;
                } else {
                    System.out.println("Invalid choice. Please choose 1 or 2.");
                }
            } else {
                System.out.println("Invalid input. Please enter a valid number (1 or 2).");
                scanner.next();
            }
        }


        long startTime = System.currentTimeMillis();

        if (algorithmChoice == 1) {
            experimentResults = runExperiments(numExperiments, sizeMB, fileName, true);
        } else if (algorithmChoice == 2) {
            experimentResults = runExperiments(numExperiments, sizeMB, fileName, false);
        }
        long endTime = System.currentTimeMillis();
        long executionTimeMillis = endTime - startTime;
        double executionTimeSeconds = (double) executionTimeMillis / 1000.0;
        System.out.println("Total execution time: " + executionTimeSeconds + " seconds");


        if (!experimentResults.isEmpty()) {
            displayExperimentSummary(experimentResults, numExperiments);
            deleteExperimentFiles(scanner, numExperiments, fileName);
            writeMetricsToFile(experimentResults);
        }
    }

    private static int getInputSize(Scanner scanner) {
        int size = 0;

        while (size <= 0 || size > 1000) {
            try {
                System.out.print("Enter the positive integer - size of the labyrinth (in cells, up to 1000): ");
                String input = scanner.next();

                if (input.matches("[1-9]\\d*")) {
                    size = Integer.parseInt(input);

                    if (size <= 0 || size > 1000) {
                        System.out.println("Labyrinth size must be an integer between 1 and 1000 cells.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a positive integer between 1 and 1000.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a positive integer between 1 and 1000.");
                scanner.next();
            }
        }

        return size;
    }

    private static int getInputNumExperiments(Scanner scanner) {
        int numExperiments = 0;

        while (numExperiments <= 0 || numExperiments > 1000) {
            try {
                System.out.print("Enter the positive integer of experiments (up to 1000): ");
                String input = scanner.next();

                if (input.matches("[1-9]\\d*")) {
                    numExperiments = Integer.parseInt(input);

                    if (numExperiments <= 0 || numExperiments > 1000) {
                        System.out.println("The number of experiments must be a positive integer between 1 and 1000.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a positive integer between 1 and 1000.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a positive integer between 1 and 1000.");
                scanner.next();
            }
        }

        return numExperiments;
    }

    private static String getFileName(Scanner scanner) {
        String fileName;
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_.-]+\\.txt$");

        do {
            System.out.print("Enter the filename for saving the labyrinth in latin (only alphanumeric characters, '.', '-', or '_'; must end with '.txt'): ");
            fileName = scanner.next();

            if (!pattern.matcher(fileName).matches()) {
                System.out.println("Invalid filename. Please use only names in latin and alphanumeric characters, '.', '-', or '_', and ensure it ends with '.txt'.");
            }
        } while (!pattern.matcher(fileName).matches());

        return fileName;
    }

    private static void displayExperimentSummary(List<PathState> experimentResults, int numExperiments) {
        int totalDeadEnds = 0;
        int totalGeneratedStates = 0;
        int totalStoredStates = 0;

        for (PathState result : experimentResults) {
            int steps = result.getSteps();
            int rowCount = result.getRowCount();
            int colCount = result.getColCount();


            int deadEnds = countDeadEnds(result);
            totalDeadEnds += deadEnds;

            int generatedStates = countGeneratedStates(result);
            totalGeneratedStates += generatedStates;

            int storedStates = rowCount * colCount;
            totalStoredStates += storedStates;
        }

        double averageDeadEnds = (double) totalDeadEnds / numExperiments;
        double averageGeneratedStates = (double) totalGeneratedStates / numExperiments;
        double averageStoredStates = (double) totalStoredStates / numExperiments;

        System.out.println("[SUMMARY]The average amount of dead ends: " + averageDeadEnds);
        System.out.println("[SUMMARY]The average amount of generated states: " + averageGeneratedStates);
        System.out.println("[SUMMARY]The average amount of the stored states: " + averageStoredStates);
        double averageStoredStatesInMB = averageStoredStates / 1024;
        System.out.println("[SUMMARY]The average amount of the stored states (MB): " + averageStoredStatesInMB);
    }

    private static void deleteExperimentFiles(Scanner scanner, int numExperiments, String fileName) {
        while (true) {
            System.out.print("Delete files (Y/N)? ");
            String confirm = scanner.next().toUpperCase();

            if (confirm.equals("Y")) {
                deleteOldFiles(numExperiments, fileName);
                System.out.println("Files were deleted.");
                break;
            } else if (confirm.equals("N")) {
                System.out.println("Files weren't deleted.");
                break;
            } else {
                System.out.println("Invalid choice. Please enter 'Y' or 'N'.");
            }
        }
    }


    private static void writeMetricsToFile(List<PathState> experimentResults) {
        String metricsFileName = "experiment_metrics.txt";
        try (FileWriter writer = new FileWriter(metricsFileName)) {
            for (PathState result : experimentResults) {
                writer.write("[EXPERIMENT] Steps: " + result.getSteps() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
