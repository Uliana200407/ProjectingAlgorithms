import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

public class MazeSolverTest {
    private MazeSolver.Maze maze;
    private int size = 5;

    @BeforeEach
    public void setUp() {
        maze = MazeSolver.generateMaze(size);
    }

    @Test
    public void testGenerateMaze() {
        assertNotNull(maze);
        assertTrue(getMazeGrid(maze).length == size);
        assertTrue(getMazeGrid(maze)[0].length == size);
    }

    @Test
    public void testCountPathNeighbors() {
        boolean[][] path = new boolean[size][size];
        int neighbors = MazeSolver.countPathNeighbors(path, 2, 2);
        assertEquals(4, neighbors);
    }

    @Test
    public void testCountDeadEnds() {
        boolean[][] path = new boolean[size][size];
        int deadEnds = MazeSolver.countDeadEnds(new MazeSolver.PathState(path, 0));
        assertEquals(0, deadEnds);
    }

    @Test
    public void testLdfsSearch() {
        int startX = 0;
        int startY = 0;
        MazeSolver.PathState result = MazeSolver.LdfsSearch(maze, startX, startY);
        assertNotNull(result);
        assertTrue(result.getSteps() > 0);
    }

    @Test
    public void testAStarSearchInvalidStart() {
        int startX = 0;
        int startY = 0;
        MazeSolver.PathState pathState = MazeSolver.AStarSearch(maze, startX, startY);

        assertNotNull(pathState);
    }

    @Test
    public void testAStarSearchNoPath() {
        int startX = 0;
        int startY = 0;
        MazeSolver.PathState pathState = MazeSolver.AStarSearch(maze, startX, startY);
        assertNotNull(pathState);
        assertFalse(isValidPath(pathState, startX, startY));
    }
    private boolean isValidPath(MazeSolver.PathState pathState, int startX, int startY) {
        boolean[][] path = pathState.path;
        int size = path.length;

        if (startX != 0 || startY != 0) {
            return false;
        }

        if (!path[size - 2][size - 2]) {
            return false;
        }
        return true;
    }
    private boolean[][] getMazeGrid(MazeSolver.Maze maze) {
        try {
            Field field = MazeSolver.Maze.class.getDeclaredField("grid");
            field.setAccessible(true);
            return (boolean[][]) field.get(maze);
        } catch (ReflectiveOperationException e) {
            System.err.println("Error accessing the 'grid' field: " + e.getMessage());
            return null;
        }
    }
}

