package org.example.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.example.logic.NetworkManager;
import org.example.logic.PlayerState;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;


public class WaitingWindowController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label label;


    private void updateLabel() {
        while (true) {
            synchronized (Integer.TYPE) {
                try {
                    Integer.TYPE.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (NetworkManager.getPlayerState() == PlayerState.IN_QUEUE) {
                Platform.runLater(() -> label.setText("Gracze mogą dołączać do gry..."));
                continue;
            }
            if (NetworkManager.getPlayerState() == PlayerState.COUNTDOWN) {
                Platform.runLater(() -> label.setText((NetworkManager.getCountdown() + 1) + "!"));
                continue;
            }
            if (NetworkManager.getPlayerState()== PlayerState.TOO_FEW_PLAYERS) {
                Platform.runLater(() -> label.setText("Jesteś sam na serwerze, poczekaj na innych graczy"));
                continue;
            }
            if (NetworkManager.getPlayerState() == PlayerState.ROUND) {
                Platform.runLater(() -> {
                    try {
                        FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("gameWindow.fxml")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        NetworkManager.getStage().setScene(new Scene(anchorPane));
        if (NetworkManager.getPlayerState() == PlayerState.IN_QUEUE)
            label.setText("Gracze mogą dołączać do gry...");
        else
            label.setText("Gra rozpocznie się po zakończeniu poprzedniej");
        Thread labelUpdateThread = new Thread(this::updateLabel);
        labelUpdateThread.start();
    }
}
