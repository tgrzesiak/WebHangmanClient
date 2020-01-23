package org.example.controllers;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.util.Duration;
import org.example.logic.NetworkManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class OpeningWindowController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label titleLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private ImageView hangmanImage;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        NetworkManager.setStage(new Stage());
        NetworkManager.getStage().setScene(new Scene(anchorPane));
        NetworkManager.getStage().setTitle("Wisielec © by Beata&Tomek");
        NetworkManager.getStage().setOnCloseRequest(windowEvent -> {
            System.out.println("Dziękujemy za grę :)");
            System.exit(0);
        });
        NetworkManager.connect();
        Thread networkThread = new Thread(NetworkManager::listenToNetwork);
        networkThread.start();
        FadeTransition fadeIn = new FadeTransition(Duration.millis(2000), anchorPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
        fadeIn.setOnFinished(this::moveToNextScene);
        NetworkManager.getStage().show();
    }

    private void moveToNextScene(ActionEvent actionEvent) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("waitingWindow.fxml")));
        } catch (IOException e) {
            System.out.println("Błąd ładowania zasobów, przepraszamy...");
        }
    }
}
