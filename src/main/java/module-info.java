module com.example.taskuri {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.taskuri to javafx.fxml;
    exports com.example.taskuri;
}