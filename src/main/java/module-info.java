module com.app.cat201app {
    requires javafx.controls;
    requires javafx.fxml;
    exports com.app.cat201app;

    opens com.app.cat201app to javafx.fxml;
}
