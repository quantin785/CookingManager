module org.example.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires com.google.gson;
    requires java.net.http;

    // Ouverture des packages pour la réflexion JavaFX / FXML
    opens org.example.frontend to javafx.fxml;
    opens org.example.frontend.controller to javafx.fxml;
    opens org.example.frontend.model to com.google.gson, javafx.base;

    exports org.example.frontend;
}
