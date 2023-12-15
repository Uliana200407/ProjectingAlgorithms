import static org.junit.Assert.*;

import com.example.lab_6.HighScore;
import com.example.lab_6.Score;
import org.junit.*;
import java.util.ArrayList;

public class Test {

    private HighScore highScore;

    @Before
    public void setUp() {
        highScore = new HighScore();
    }

    @org.junit.Test
    public void testInitializationWithExistingFile() {
        highScore = new HighScore();
        ArrayList<Score> scores = highScore.getScores();
        assertEquals("Expected number of scores", 5, scores.size());
    }

    @org.junit.Test
    public void testLoadHighScoreWithCorruptedData() {
        highScore = new HighScore();
        ArrayList<Score> scores = highScore.getScores();
    }

    @org.junit.Test
    public void testScoresLimit() {
        for (int i = 1; i <= 10; i++) {
            highScore.submitHighScore("User" + i, i * 10);
        }

        ArrayList<Score> scores = highScore.getScores();

        assertEquals("Scores list should only contain top 5 scores", 5, scores.size());
    }

    @org.junit.Test
    public void testDataPersistence() {
        highScore.submitHighScore("PersistentUser", 200);

        HighScore newHighScore = new HighScore();
        ArrayList<Score> scores = newHighScore.getScores();

        assertEquals("PersistentUser", scores.get(0).getName());
        assertEquals(200, scores.get(0).getScore());
    }
}
