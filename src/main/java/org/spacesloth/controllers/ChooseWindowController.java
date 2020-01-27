package org.spacesloth.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.spacesloth.logic.NetworkManager;
import org.spacesloth.logic.PlayerState;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static org.spacesloth.logic.NetworkManager.*;

public class ChooseWindowController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label titleLabel;

    @FXML
    private Label helperLabel;

    @FXML
    private Button buttonYes;

    @FXML
    private Button buttonNo;

    @FXML
    private void continueGame() {
        connect();
        Thread networkThread = new Thread(NetworkManager::listenToNetwork);
        networkThread.start();
        setPlayerState(PlayerState.WAIT_FOR_BEGINNING);
        try {
            FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("waitingWindow.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exitGame() {
        NetworkManager.exitGame();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getStage().setScene(new Scene(anchorPane));
    }
}
