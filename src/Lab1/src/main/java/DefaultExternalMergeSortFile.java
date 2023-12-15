import java.io.*;
import java.util.*;
import java.util.concurrent.*;

interface FileSplitter {
    void splitInputFile(String inputFile, int chunkSize) throws IOException;
}

interface FileMerger {
    void mergeSortedChunks(String outputFile) throws IOException;
}

class DefaultFileSplitter implements FileSplitter {
    @Override
    public void splitInputFile(String inputFile, int chunkSize) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            int[] chunk = new int[chunkSize];
            int index = 0;
            int chunkNum = 0;

            long startTime = System.currentTimeMillis();

            String line;
            while ((line = br.readLine()) != null) {
                chunk[index++] = Integer.parseInt(line);

                if (index == chunkSize) {
                    sortAndWriteChunk(chunk, "chunk_" + chunkNum + ".txt");
                    chunkNum++;
                    index = 0;
                }
            }

            if (index > 0) {
                sortAndWriteChunk(chunk, "chunk_" + chunkNum + ".txt");
            }

            long endTime = System.currentTimeMillis();
            System.out.println("Splitting time: " + (endTime - startTime) + " ms");
        }
    }

    private void sortAndWriteChunk(int[] chunk, String fileName) throws IOException {
        Arrays.sort(chunk);
        writeChunkToFile(chunk, fileName);
    }

    private void writeChunkToFile(int[] chunk, String fileName) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (int num : chunk) {
                bw.write(Integer.toString(num));
                bw.newLine();
            }
        }
    }
}

class DefaultFileMerger implements FileMerger {
    @Override
    public void mergeSortedChunks(String outputFile) throws IOException {
        File[] chunkFiles = findChunkFiles();
        int[] minValues = new int[chunkFiles.length];
        BufferedReader[] readers = openReaders(chunkFiles);
        BufferedWriter bw = openWriter(outputFile);

        readInitialValues(minValues, readers);

        long startTime = System.currentTimeMillis();

        mergeChunks(minValues, readers, bw);

        long endTime = System.currentTimeMillis();
        System.out.println("Merging time: " + (endTime - startTime) + " ms");

        closeResources(readers, bw);
        deleteChunkFiles(chunkFiles);
    }
    private void closeResources(BufferedReader[] readers, BufferedWriter bw) throws IOException {
        bw.close();
        for (BufferedReader reader : readers) {
            reader.close();
        }
    }

    private BufferedWriter openWriter(String outputFile) throws IOException {
        return new BufferedWriter(new FileWriter(outputFile));
    }

    private void mergeChunks(int[] minValues, BufferedReader[] readers, BufferedWriter bw) throws IOException {
        while (true) {
            int minIndex = findMinIndex(minValues);
            if (minIndex == -1) {
                break;
            }
            writeMinValue(minValues[minIndex], bw);
            updateMinValue(minIndex, minValues, readers);
        }
    }

    private File[] findChunkFiles() {
        return new File(".").listFiles((dir, name) -> name.startsWith("chunk_"));
    }

    private void deleteChunkFiles(File[] chunkFiles) {
        for (File file : chunkFiles) {
            try {
                if (file.delete()) {
                    System.out.println("File deleted successfully: " + file.getName());
                } else {
                    System.err.println("Failed to delete file: " + file.getName());
                }
            } catch (SecurityException e) {
                System.err.println("SecurityException while deleting file: " + file.getName());
                e.printStackTrace();
            }
        }
    }

    private BufferedReader[] openReaders(File[] chunkFiles) throws FileNotFoundException {
        BufferedReader[] readers = new BufferedReader[chunkFiles.length];
        for (int i = 0; i < chunkFiles.length; i++) {
            readers[i] = new BufferedReader(new FileReader(chunkFiles[i]));
        }
        return readers;
    }

    private void readInitialValues(int[] minValues, BufferedReader[] readers) throws IOException {
        for (int i = 0; i < readers.length; i++) {
            minValues[i] = Integer.parseInt(readers[i].readLine());
        }
    }

    private int findMinIndex(int[] array) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
                minIndex = i;
            }
        }
        return minIndex;
    }

    private void writeMinValue(int minValue, BufferedWriter bw) throws IOException {
        bw.write(Integer.toString(minValue));
        bw.newLine();
    }

    private void updateMinValue(int minIndex, int[] minValues, BufferedReader[] readers) throws IOException {
        String nextLine = readers[minIndex].readLine();
        if (nextLine == null) {
            minValues[minIndex] = Integer.MAX_VALUE;
        } else {
            minValues[minIndex] = Integer.parseInt(nextLine);
        }
    }
}

public class DefaultExternalMergeSortFile {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the file: ");
        String fileName = scanner.nextLine();

        long fileSizeInBytes = getFileSizeFromUser(scanner);

        try {
            long startTime = System.currentTimeMillis();
            createAndPopulateLargeFile(fileName, fileSizeInBytes);
            long endTime = System.currentTimeMillis();
            System.out.println("File was populated successfully - " + fileName);
            long elapsedTime = endTime - startTime;
            System.out.println("File creation time: " + (elapsedTime / 60000) + " minutes");

            startTime = System.currentTimeMillis();
            ExternalFileSorter externalFileSorter = new ExternalFileSorter();
            externalFileSorter.externalMergeSort(fileName, "sorted_" + fileName, 1000000);
            endTime = System.currentTimeMillis();
            System.out.println("Sorting was ended.");
            elapsedTime = endTime - startTime;
            System.out.println("Sorting time: " + (endTime - startTime) + " ms");
            System.out.println("Sorting time: " + (elapsedTime / 60000) + " minutes");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static long getFileSizeFromUser(Scanner scanner) {
        long fileSizeInBytes;
        while (true) {
            System.out.print("Enter the size of the file in mb (integer): ");
            try {
                String input = scanner.next();
                fileSizeInBytes = Long.parseLong(input) * 1024L * 1024L;

                if (fileSizeInBytes <= 0) {
                    throw new IllegalArgumentException("The size of the file should be bigger than zero");
                }

                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        return fileSizeInBytes;
    }

    public static void createAndPopulateLargeFile(String fileName, long fileSizeInBytes) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            long totalNumbers = fileSizeInBytes / 4;
            Random random = new Random();
            for (long i = 0; i < totalNumbers; i++) {
                int randomNumber = random.nextInt();
                bw.write(Integer.toString(randomNumber));
                bw.newLine();
            }
        }
    }
}

class ExternalFileSorter {
    private final FileSplitter fileSplitter;
    private final FileMerger fileMerger;

    public ExternalFileSorter() {
        this.fileSplitter = new DefaultFileSplitter();
        this.fileMerger = new DefaultFileMerger();
    }

    public void externalMergeSort(String inputFile, String outputFile, int chunkSize) throws IOException {
        fileSplitter.splitInputFile(inputFile, chunkSize);
        fileMerger.mergeSortedChunks(outputFile);
    }
}
