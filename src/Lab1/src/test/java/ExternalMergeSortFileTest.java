import static org.junit.jupiter.api.Assertions.*;
import org.testng.annotations.Test;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExternalMergeSortFileTest {


        @Test
        public void testExternalMergeSort() throws IOException {
            String inputFile = "inputFile.txt";
            String outputFile = "outputFile.txt";

            ExternalMergeSortFile.createAndPopulateLargeFile(inputFile, 1024 * 1024);

            ExternalMergeSortFile.externalMergeSort(inputFile, outputFile, 1000000);
            File outputFileObj = new File(outputFile);
            assertTrue(outputFileObj.exists());

            List <Integer> values;

            try (Stream <String> lines = Files.lines( Paths.get(outputFile))) {
                values = lines.map(Integer::parseInt).collect( Collectors.toList());
            }

            for (int i = 1; i < values.size(); i++) {
                assertTrue(values.get(i) >= values.get(i - 1));
            }

            outputFileObj.delete();
            new File(inputFile).delete();
        }



    @Test
    public void testChunkFileMerger() throws IOException {
        String inputFile = "inputFile.txt";
        ExternalMergeSortFile.createAndPopulateLargeFile(inputFile, 1024 * 1024); // 1 MB

        String outputFile = "outputFile.txt";
        int chunkSize = 1000;
        ChunkFileSplitter.splitInputFile(inputFile, chunkSize);

        ChunkFileMerger.mergeSortedChunks(outputFile);

        File outputFileObj = new File(outputFile);
        assertTrue(outputFileObj.exists());

        BufferedReader reader = new BufferedReader(new FileReader(outputFile));
        int prevValue = Integer.MIN_VALUE;
        String line;
        while ((line = reader.readLine()) != null) {
            int value = Integer.parseInt(line);
            assertTrue(value >= prevValue);
            prevValue = value;
        }
        reader.close();

        outputFileObj.delete();
        new File(inputFile).delete();
    }


    private void assertFileIsSorted(File file) throws IOException {
        AtomicInteger prevValue = new AtomicInteger ( Integer.MIN_VALUE );
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            assertAll(() -> {
                String line;
                while ((line = reader.readLine()) != null) {
                    int value = Integer.parseInt(line);
                    assertTrue(value >= prevValue.get () );
                    prevValue.set ( value );
                }
            });
        }
    }

}
