package com.example.labwork3;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static com.example.labwork3.HelloApplication.*;

public class GUI {

    @FXML
    private Label fileStatusLabel;
    @FXML
    private TextField searchKeyField;

    @FXML
    private Label filePathLabel;

    @FXML
    private TableView<HelloApplication.DataRecord> table;

    @FXML
    private void initialize() {
        fileStatusLabel.setText("File is not selected");
        filePathLabel.setText("");
        setupTableColumns();
    }

    private void setupTableColumns() {
        table.getColumns().clear();
        TableColumn<HelloApplication.DataRecord, Integer> keyColumn = new TableColumn<>("Key");
        TableColumn<HelloApplication.DataRecord, String> valueColumn = new TableColumn<>("Value");
        keyColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        table.getColumns().addAll(keyColumn, valueColumn);
    }

    @FXML
    private void selectFile() {
        Stage stage = (Stage) fileStatusLabel.getScene().getWindow();
        File selectedFile = chooseFile("Data type", "*.dat");
        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            if (isValidFilePath(filePath)) {
                setFilename(filePath);
                fileStatusLabel.setText("Selected file:");
                filePathLabel.setText(filePath);
                updateDataTable();
            } else {
                clearFileSelection();
            }
        }
    }

    private File chooseFile(String extensionDescription, String extensionFilter) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(extensionDescription, extensionFilter));
        return fileChooser.showOpenDialog(null);
    }

    private boolean isValidFilePath(String filePath) {
        return Files.exists(Paths.get(filePath)) && filePath.endsWith(".dat") && filePath.split("\\.").length == 2;
    }

    private void clearFileSelection() {
        fileStatusLabel.setText("File is not selected");
        filePathLabel.setText("");
    }

    @FXML
    private void searchByKey() {
        if (filename != null) {
            String keyText = searchKeyField.getText();
            if (!keyText.isEmpty()) {
                try {
                    int searchKey = Integer.parseInt(keyText);
                    List<DataRecord> records = readDataFromFile();
                    boolean found = searchAndDisplayKey(searchKey, records);

                    if (!found) {
                        showAlert("Search Failed", "No record with the entered key was found.");
                    }
                } catch (NumberFormatException e) {
                    showAlert("Invalid Key Format", "Please enter a valid integer for the key.");
                }
            }
        }
    }

    private boolean searchAndDisplayKey(int searchKey, List<DataRecord> records) {
        for (DataRecord record : records) {
            if (record.getKey() == searchKey) {
                displayKeyFoundMessage(searchKey, record.getValue());
                return true;
            }
        }
        return false;
    }

    private void displayKeyFoundMessage(int key, String value) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Key Found");
        alert.setHeaderText(null);
        alert.setContentText("Key " + key + " was found with the value: " + value);

        alert.showAndWait();
    }

    @FXML
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

    private void processAddDataInput(String input) {
        String[] parts = input.split(" ");
        if (parts.length == 2) {
            try {
                int key = Integer.parseInt(parts[0]);
                String value = parts[1];
                writeDataToFile(key, value);
                System.out.println("Data added successfully.");
                updateDataTable();
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Key must be an integer. Please try again.");
            }
        } else {
            showAlert("Invalid Input", "Please enter Key and Value separated by space.");
        }
    }

    @FXML
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
                String newValue = parts[1];
                List<DataRecord> records = readDataFromFile();
                boolean updated = updateDataForKey(key, newValue, records);

                if (updated) {
                    System.out.println("Data updated successfully.");
                    updateDataTable();
                } else {
                    showAlert("Update Failed", "Key not found. Data update failed.");
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Input", "Key must be an integer.");
            }
        } else {
            showAlert("Invalid Input", "Please enter Key and New Value separated by space.");
        }
    }

    private boolean updateDataForKey(int key, String newValue, List<DataRecord> records) {
        for (DataRecord record : records) {
            if (record.getKey() == key) {
                record.setValue(newValue);
                writeAllDataToFile(records);
                return true;
            }
        }
        return false;
    }

    @FXML
    private void displayData() {
        if (filename != null) {
            updateDataTable();
        }
    }

    private void updateDataTable() {
        List<HelloApplication.DataRecord> data = readDataFromFile();
        table.getItems().clear();
        table.getItems().addAll(data);
    }

    @FXML
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
            List<DataRecord> records = readDataFromFile();
            boolean deleted = deleteDataForKey(keyToDelete, records);

            if (deleted) {
                System.out.println("Data deleted successfully.");
                updateDataTable();
            } else {
                showAlert("Delete Failed", "Key not found. No data deleted.");
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Key must be an integer.");
        }
    }

    private boolean deleteDataForKey(int keyToDelete, List<DataRecord> records) {
        Iterator<DataRecord> iterator = records.iterator();
        while (iterator.hasNext()) {
            DataRecord record = iterator.next();
            if (record.getKey() == keyToDelete) {
                iterator.remove();
                writeAllDataToFile(records);
                return true;
            }
        }
        return false;
    }

    @FXML
    private void clearFile() {
        if (HelloApplication.getFilename() != null) {
            try {
                Files.deleteIfExists(Paths.get(HelloApplication.getFilename()));
                HelloApplication.setFilename(null);
                System.out.println("File cleared.");
                table.getItems().clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void createFile() {
        File file = chooseFile("Data type", "*.dat");
        if (file != null) {
            String filePath = file.getAbsolutePath();
            if (isValidFilePath(filePath)) {
                createNewFile(filePath);
            } else {
                System.out.println("Invalid file extension. Please choose a .dat file.");
            }
        }
    }

    private void createNewFile(String filePath) {
        try {
            Files.createFile(Paths.get(filePath));
            setFilename(filePath);
            fileStatusLabel.setText("Selected file:");
            filePathLabel.setText(filePath);
            updateDataTable();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<String> getInputFromUser(String title, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(content);
        return dialog.showAndWait();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
