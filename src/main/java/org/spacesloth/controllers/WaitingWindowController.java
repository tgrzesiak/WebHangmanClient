package org.spacesloth.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.spacesloth.logic.PlayerState;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static org.spacesloth.logic.NetworkManager.*;


public class WaitingWindowController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getStage().setScene(new Scene(anchorPane));
        if (getPlayerState() == PlayerState.IN_QUEUE) label.setText("Gracze mogą dołączać do gry...");
        if (getPlayerState() == PlayerState.END_OF_ROUND) label.setText("Następna runda zaraz się rozpocznie");
        if (getPlayerState() == PlayerState.WAIT_FOR_BEGINNING) label.setText("Gra rozpocznie się po zakończeniu poprzedniej");
        Thread labelUpdateThread = new Thread(this::updateLabel);
        labelUpdateThread.start();
    }

    private void updateLabel() {
        while (true) {
            synchronized (Integer.TYPE) {
                try {
                    Integer.TYPE.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (getPlayerState() == PlayerState.IN_QUEUE) {
                Platform.runLater(() -> label.setText("Gracze mogą dołączać do gry..."));
                continue;
            }
            if (getPlayerState() == PlayerState.COUNTDOWN) {
                Platform.runLater(() -> label.setText((getCountdown() + 1) + "!"));
                continue;
            }
            if (getPlayerState()== PlayerState.TOO_FEW_PLAYERS) {
                Platform.runLater(() -> label.setText("Jesteś sam na serwerze, poczekaj na innych graczy"));
                continue;
            }
            if (getPlayerState() == PlayerState.ROUND) {
                Platform.runLater(() -> {
                    try {
                        FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("gameWindow.fxml")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            }
            if (getPlayerState() == PlayerState.WINNER) {
                disconnect();
                Platform.runLater(() -> {
                    try {
                        FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("chooseWindow.fxml")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            }
        }
    }

}
