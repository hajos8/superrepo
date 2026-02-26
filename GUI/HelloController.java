package com.example.demo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

public class HelloController {
    //public static ArrayList<TODO> lista = new ArrayList<>();

    @FXML public BorderPane container;

    @FXML public void fileOpener() {
        Stage currentStage = (Stage) container.getScene().getWindow();

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Fálj kiválasztása...");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV fájl", "*.csv"));
            File selectedFile = fileChooser.showOpenDialog(currentStage);

            if (selectedFile != null) {
                //lista = CSVParser.parse(selectedFile.getPath());
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    @FXML public void exitPlatform () {
        Platform.exit();
    }


    @FXML public void handleAbout () {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Névjegy");
        alert.setHeaderText("");
        alert.setContentText("TODO v1.0.0\n(C) Kandó");
        alert.showAndWait();
    }
}