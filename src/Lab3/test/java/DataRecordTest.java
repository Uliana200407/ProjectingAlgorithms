import com.example.labwork3.HelloApplication;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DataRecordTest {

    private static final String TEST_DATA = "1 testValue";

    @TempDir
    static Path sharedTempDir;

    @BeforeEach
    void setUp() {
        HelloApplication.setFilename(sharedTempDir.resolve("test.dat").toString());
    }
    @Test
    void testCreateDataRecord() {
        HelloApplication.DataRecord record = new HelloApplication.DataRecord (1, "value");
        assertNotNull(record);
        assertEquals(1, record.getKey());
        assertEquals("value", record.getValue());
    }

    @Test
    void testSetValue() {
        HelloApplication.DataRecord record = new HelloApplication.DataRecord (1, "initialValue");
        record.setValue("newValue");
        assertEquals("newValue", record.getValue());
    }

    @Test
    void testWriteDataToFile() throws Exception {
        HelloApplication.writeDataToFile(1, "testValue");

        List <String> lines = Files.readAllLines( Paths.get(HelloApplication.getFilename()));
        assertTrue(lines.contains(TEST_DATA));
    }

    @Test
    void testWriteAllData() throws Exception {
        List<HelloApplication.DataRecord> records = new ArrayList <> ();
        records.add(new HelloApplication.DataRecord(1, "testValue"));

        HelloApplication.writeAllDataToFile(records);

        List<String> lines = Files.readAllLines(Paths.get(HelloApplication.getFilename()));
        assertEquals(1, lines.size());
        assertTrue(lines.contains(TEST_DATA));
    }

    @Test
    void testReadDataFromFile() throws Exception {
        Files.write(Paths.get(HelloApplication.getFilename()), Collections.singletonList(TEST_DATA));

        List<HelloApplication.DataRecord> records = HelloApplication.readDataFromFile();

        assertFalse(records.isEmpty());
        HelloApplication.DataRecord record = records.get(0);
        assertEquals(1, record.getKey());
        assertEquals("testValue", record.getValue());
    }
    @Test
    public void testSetAndGetFilename() {
        HelloApplication.setFilename("test.dat");
        assertEquals("test.dat", HelloApplication.getFilename());
    }

}
