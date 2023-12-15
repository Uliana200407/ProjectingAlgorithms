import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultExternalFileSorterTest {
    private final String testFileName = "testFile.txt";
    private final String sortedFileName = "sorted_testFile.txt";
    private final int chunkSize = 100;

    @Test
    public void testFileSplitter() throws IOException {
        FileSplitter fileSplitter = new DefaultFileSplitter();

        createTestFile(testFileName);

        fileSplitter.splitInputFile(testFileName, chunkSize);
        File[] chunkFiles = findChunkFiles();
        assertNotNull(chunkFiles);
        assertTrue(chunkFiles.length > 0);

        for (File chunkFile : chunkFiles) {
            int[] numbers = readNumbersFromFile(chunkFile);
            assertNotNull(numbers);
            assertTrue(numbers.length <= chunkSize);
        }

        cleanupTestFiles(chunkFiles);
    }



    private void createTestFile(String fileName) throws IOException {
        // Створення тестового файлу з даними
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write("5");
            bw.newLine();
            bw.write("2");
            bw.newLine();
            bw.write("8");
            bw.newLine();
            bw.write("1");
            bw.newLine();
            bw.write("3");
            bw.newLine();
        }
    }

    private File[] findChunkFiles() {
        return new File(".").listFiles((dir, name) -> name.startsWith("chunk_"));
    }

    private void cleanupTestFiles(File[] files) {
        for (File file : files) {
            if (file.exists() && !file.delete()) {
                System.err.println("Failed to delete file: " + file.getName());
            }
        }
    }


    private int[] readNumbersFromFile(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int[] numbers = new int[100];
            int index = 0;
            while ((line = br.readLine()) != null) {
                if (index == numbers.length) {
                    numbers = Arrays.copyOf(numbers, numbers.length * 2);
                }
                numbers[index++] = Integer.parseInt(line);
            }
            return Arrays.copyOf(numbers, index);
        }
    }
    @Test
    public void testFileMerger() throws IOException {
        FileMerger fileMerger = new DefaultFileMerger();

        createTestFile(testFileName);

        FileSplitter fileSplitter = new DefaultFileSplitter();
        fileSplitter.splitInputFile(testFileName, chunkSize);
        File[] chunkFiles = findChunkFiles();
        assertNotNull(chunkFiles);
        assertTrue(chunkFiles.length > 0);

        fileMerger.mergeSortedChunks(sortedFileName);
        File sortedFile = new File(sortedFileName);
        assertTrue(sortedFile.exists());

        cleanupTestFiles(chunkFiles);
        sortedFile.delete();
        }

    }

