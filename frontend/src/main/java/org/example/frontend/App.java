package org.example.frontend;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Point d'entrée de l'application JavaFX.
 * Lance la fenêtre principale et applique le thème AtlantaFX.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Applique le thème moderne AtlantaFX PrimerLight
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        FXMLLoader loader = new FXMLLoader(App.class.getResource("fxml/main.fxml"));
        Scene scene = new Scene(loader.load(), 1280, 800);
        scene.getStylesheets().add(App.class.getResource("css/style.css").toExternalForm());

        stage.setTitle("RecetteApp — Gestion de Recettes");
        stage.setScene(scene);
        stage.setMinWidth(960);
        stage.setMinHeight(640);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
