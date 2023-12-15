package com.example.labwork3;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class HelloApplication extends Application {
    public static String filename;
    private TextField searchKeyField;

    private static TableView<DataRecord> table;
    public static TextArea fileContentsTextArea;
    private Label fileStatusLabel;

    private Label filePathLabel;

    private Button addDataButton;
    private Button updateDataButton;
    private Button deleteDataButton;
    private Button searchButton;
    private boolean dataAdded = false;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Laboratory work 3");

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem openMenuItem = new MenuItem("Open");
        MenuItem clearMenuItem = new MenuItem("Clear File");
        MenuItem createFileMenuItem = new MenuItem("Create File");

        fileMenu.getItems().addAll(openMenuItem, clearMenuItem, createFileMenuItem);
        menuBar.getMenus().add(fileMenu);

        openMenuItem.setOnAction(event -> selectFile(primaryStage));
        clearMenuItem.setOnAction(event -> clearFile());
        createFileMenuItem.setOnAction(event -> createFile(primaryStage));

        VBox vbox = new VBox();
        vbox.setSpacing(10);

        fileStatusLabel = new Label("File is not selected");
        filePathLabel = new Label("");

        table = new TableView<>();
        TableColumn<DataRecord, Integer> keyColumn = new TableColumn<>("Key");
        TableColumn<DataRecord, String> valueColumn = new TableColumn<>("Value");
        keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        table.getColumns().addAll(keyColumn, valueColumn);

        fileContentsTextArea = new TextArea();
        fileContentsTextArea.setWrapText(true);

        addDataButton = new Button("Add Data");
        updateDataButton = new Button("Update Data");
        deleteDataButton = new Button("Delete Data");

        addDataButton.setDisable(true);
        updateDataButton.setDisable(true);
        deleteDataButton.setDisable(true);

        addDataButton.setOnAction(event -> addData());
        updateDataButton.setOnAction(event -> updateData());
        deleteDataButton.setOnAction(event -> deleteData());

        searchKeyField = new TextField();
        searchButton = new Button("Пошук");

        addDataButton.setDisable(true);
        updateDataButton.setDisable(true);
        deleteDataButton.setDisable(true);
        searchButton.setDisable(true);

        vbox.getChildren().addAll(fileStatusLabel, filePathLabel, menuBar, addDataButton, updateDataButton, deleteDataButton, searchKeyField, searchButton, table, fileContentsTextArea);

        searchButton.setOnAction(event -> searchByKey());

        root.setTop(vbox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void setFilename(String filename) {
        HelloApplication.filename = filename;
    }

    public static String getFilename() {
        return filename;
    }

    private void createFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Data type", "*.dat"));
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            String filePath = file.getAbsolutePath();
            if (filePath.endsWith(".dat") && filePath.split("\\.").length == 2) {
                try {
                    Files.createFile(Paths.get(filePath));
                    setFilename(filePath);
                    fileStatusLabel.setText("Selected file:");
                    filePathLabel.setText(filePath);
                    table.getItems().clear();

                    addDataButton.setDisable(false);
                    updateDataButton.setDisable(false);
                    deleteDataButton.setDisable(false);
                    searchButton.setDisable(false);

                    dataAdded = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (!filePath.contains(".")) {
                fileStatusLabel.setText("File is not selected");
            } else {
                System.out.println("Invalid file extension. Please choose a .dat file.");
            }
        }
    }

    private void searchByKey() {
        if (filename != null) {
            String keyText = searchKeyField.getText();
            if (!keyText.isEmpty()) {
                try {
                    int searchKey = Integer.parseInt(keyText);
                    List<DataRecord> records = readDataFromFile();
                    boolean found = false;

                    for (DataRecord record : records) {
                        if (record.getKey() == searchKey) {
                            displayKeyFoundMessage(searchKey, record.getValue());
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        showAlert("Search Failed", "No record with the entered key was found.");
                    }
                } catch (NumberFormatException e) {
                    showAlert("Invalid Key Format", "Please enter a valid integer for the key.");
                }
            }
        } else {
            showAlert("Data Not Added", "Please add data before performing search, update, or delete operations.");
        }
    }


    private void displayKeyFoundMessage(int key, String value) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Key Found");
        alert.setHeaderText(null);
        alert.setContentText("Key " + key + " was found with the value: " + value);

        alert.showAndWait();
    }

    private void selectFile(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Data type", "*.dat"));
        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            if (Files.exists(Paths.get(filePath)) && filePath.endsWith(".dat") && filePath.split("\\.").length == 2) {
                filename = filePath;
                table.getItems().clear();
                displayData();
            } else if (!filePath.contains(".")) {
                System.out.println("File is not selected");
            } else {
                System.out.println("Can't find such file");
            }
        }
    }

    public static void clearFile() {
        if (filename != null) {
            try {
                Files.deleteIfExists(Paths.get(filename));
                filename = null;
                System.out.println("File cleared.");
                table.getItems().clear();
                fileContentsTextArea.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void displayData() {
        if (filename != null) {
            List<DataRecord> records = readDataFromFile();
            table.getItems().addAll(records);

            StringBuilder fileContents = new StringBuilder();
            try {
                Scanner scanner = new Scanner(new File(filename));
                while (scanner.hasNextLine()) {
                    fileContents.append(scanner.nextLine()).append("\n");
                }
                scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileContentsTextArea.setText(fileContents.toString());
        }
    }

    public static List<DataRecord> readDataFromFile() {
        List<DataRecord> records = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(filename));
            while (scanner.hasNext()) {
                int key = scanner.nextInt();
                String value = scanner.next();
                records.add(new DataRecord(key, value));
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    private void addData() {
        if (filename != null) {
            Optional<String> input = getInputFromUser("Add Data", "Enter Key and Value (separated by space):");
            if (input.isPresent()) {
                processAddDataInput(input.get());
            }
        } else {
            showAlert("Data Not Added", "Please add data before updating, searching, or deleting.");
        }
    }

    private Optional<String> getInputFromUser(String title, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(content);
        return dialog.showAndWait();
    }

    private void processAddDataInput(String input) {
        String[] parts = input.split(" ");
        if (parts.length == 2) {
            try {
                int key = Integer.parseInt(parts[0]);
                if (!parts[0].equals(String.valueOf(key))) {
                    throw new NumberFormatException("Key cannot have leading zeros");
                }
                String value = parts[1];

                List<DataRecord> records = readDataFromFile();
                boolean keyExists = records.stream().anyMatch(record -> record.getKey() == key);

                if (keyExists) {
                    showAlert("Key Already Exists", "The key already exists. Please enter a different key.");
                } else {
                    writeDataToFile(key, value);
                    System.out.println("Data added successfully.");
                    table.getItems().clear();
                    displayData();

                    dataAdded = true;

                    updateButtonState();
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Key must be a non-zero-padded integer. Please try again.");
            }
        } else {
            showAlert("Invalid Input", "Please enter Key and Value separated by space.");
        }
    }

    private void updateData() {
        if (filename != null) {
            Optional<String> input = getInputFromUser("Update Data", "Enter Key and New Value (separated by space):");
            if (input.isPresent()) {
                processUpdateDataInput(input.get());
            }
        } else {
            showAlert("Data Not Added", "Please add data before updating, searching, or deleting.");
        }
    }

    private void processUpdateDataInput(String input) {
        String[] parts = input.split(" ");
        if (parts.length == 2) {
            try {
                int key = Integer.parseInt(parts[0]);
                if (!parts[0].equals(String.valueOf(key))) {
                    throw new NumberFormatException("Key cannot have leading zeros");
                }
                String newValue = parts[1];

                List<DataRecord> records = readDataFromFile();
                boolean updated = false;

                for (DataRecord record : records) {
                    if (record.getKey() == key) {
                        record.setValue(newValue);
                        updated = true;
                        break;
                    }
                }

                if (updated) {
                    writeAllDataToFile(records);
                    showAlert("Success", "Data updated successfully.");
                    table.getItems().clear();
                    displayData();
                } else {
                    showAlert("Update Failed", "Key not found. Data update failed.");
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Key must be a non-zero-padded integer.");
            }
        } else {
            showAlert("Invalid Input", "Please enter Key and New Value separated by space.");
        }
    }

    private void deleteData() {
        if (filename != null) {
            Optional<String> input = getInputFromUser("Delete Data", "Enter Key to delete:");
            if (input.isPresent()) {
                processDeleteDataInput(input.get());
            }
        } else {
            showAlert("Data Not Added", "Please add data before updating, searching, or deleting.");
        }
    }

    private void processDeleteDataInput(String input) {
        try {
            int keyToDelete = Integer.parseInt(input);
            if (!input.equals(String.valueOf(keyToDelete))) {
                throw new NumberFormatException("Key cannot have leading zeros");
            }
            List<DataRecord> records = readDataFromFile();
            boolean found = false;

            Iterator<DataRecord> iterator = records.iterator();
            while (iterator.hasNext()) {
                DataRecord record = iterator.next();
                if (record.getKey() == keyToDelete) {
                    iterator.remove();
                    found = true;
                    break;
                }
            }

            if (found) {
                writeAllDataToFile(records);
                showAlert("Success", "Data deleted successfully.");
                table.getItems().clear();
                displayData();
            } else {
                showAlert("Delete Failed", "Key not found. No data deleted.");
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Key must be a non-zero-padded integer.");
        }
    }


    private void updateButtonState() {
        addDataButton.setDisable(false);
        updateDataButton.setDisable(!dataAdded);
        deleteDataButton.setDisable(!dataAdded);
        searchButton.setDisable(!dataAdded);
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void writeDataToFile(int key, String value) {
        if (filename != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
                writer.println(key + " " + value);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeAllDataToFile(List<DataRecord> records) {
        if (filename != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename, false))) {
                for (DataRecord record : records) {
                    writer.println(record.getKey() + " " + record.getValue());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class DataRecord {
        private final int key;
        private String value;

        public DataRecord(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public int getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}