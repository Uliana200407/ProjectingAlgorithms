module com.example.labwork3 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.example.labwork3 to javafx.fxml;
    exports com.example.labwork3;
}