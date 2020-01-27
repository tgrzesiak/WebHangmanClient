package org.spacesloth.controllers;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.spacesloth.logic.NetworkManager;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static org.spacesloth.logic.NetworkManager.*;

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
        setStage(new Stage());
        getStage().setScene(new Scene(anchorPane));
        getStage().setTitle("Wisielec © by Beata&Tomek");
        connect();
        getStage().setOnCloseRequest(windowEvent -> {
            exitGame();
        });
        Thread networkThread = new Thread(NetworkManager::listenToNetwork);
        networkThread.start();
        FadeTransition fadeIn = new FadeTransition(Duration.millis(2000), anchorPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
        fadeIn.setOnFinished(this::moveToNextScene);
        getStage().show();
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
