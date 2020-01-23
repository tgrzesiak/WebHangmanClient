package org.example.controllers;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import org.example.logic.NetworkManager;
import org.example.logic.PlayerState;

import java.io.IOException;
import java.net.URL;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class GameWindowController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label wordLabel;

    @FXML
    private Button buttonA;

    @FXML
    private Button buttonĄ;

    @FXML
    private Button buttonB;

    @FXML
    private Button buttonC;

    @FXML
    private Button buttonĆ;

    @FXML
    private Button buttonD;

    @FXML
    private Button buttonE;

    @FXML
    private Button buttonĘ;

    @FXML
    private Button buttonF;

    @FXML
    private Button buttonG;

    @FXML
    private Button buttonH;

    @FXML
    private Button buttonI;

    @FXML
    private Button buttonJ;

    @FXML
    private Button buttonK;

    @FXML
    private Button buttonL;

    @FXML
    private Button buttonŁ;

    @FXML
    private Button buttonM;

    @FXML
    private Button buttonN;

    @FXML
    private Button buttonŃ;

    @FXML
    private Button buttonO;

    @FXML
    private Button buttonÓ;

    @FXML
    private Button buttonP;

    @FXML
    private Button buttonR;

    @FXML
    private Button buttonS;

    @FXML
    private Button buttonŚ;

    @FXML
    private Button buttonT;

    @FXML
    private Button buttonU;

    @FXML
    private Button buttonW;

    @FXML
    private Button buttonY;

    @FXML
    private Button buttonZ;

    @FXML
    private Button buttonŹ;

    @FXML
    private Button buttonŻ;

    @FXML
    private void newWord(MouseEvent event) {
        Object target = event.getTarget();
        Button button;
        if (target instanceof Button) {
            button = (Button)target;
        } else {
            if (target instanceof Text) {
                Text buttonText = (Text) target;
                button = findButton(buttonText.getText());

            } else {
                return;
            }
        }
        NetworkManager.getRound().updateHiddenWord(button.getText().toCharArray()[0]);
        wordLabel.setText(NetworkManager.getRound().getHiddenWord());
        if (NetworkManager.getRound().getHiddenLettersCounter() == 0) {
            for (Button but : buttons) {
                but.setDisable(true);
            }
            wordLabel.setTextFill(Paint.valueOf("green"));
            wordLabel.setUnderline(true);
        }
        else button.setDisable(true);
    }


    private ArrayList<Button> buttons;

    private Button findButton(String text) {
        for (Button button : buttons) {
            if (button.getText().equals(text)) return button;
        }
        return buttonZ;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttons = new ArrayList<>();
        buttons.add(buttonA);
        buttons.add(buttonĄ);
        buttons.add(buttonB);
        buttons.add(buttonC);
        buttons.add(buttonĆ);
        buttons.add(buttonD);
        buttons.add(buttonE);
        buttons.add(buttonĘ);
        buttons.add(buttonF);
        buttons.add(buttonG);
        buttons.add(buttonH);
        buttons.add(buttonI);
        buttons.add(buttonJ);
        buttons.add(buttonK);
        buttons.add(buttonL);
        buttons.add(buttonŁ);
        buttons.add(buttonM);
        buttons.add(buttonN);
        buttons.add(buttonŃ);
        buttons.add(buttonO);
        buttons.add(buttonÓ);
        buttons.add(buttonP);
        buttons.add(buttonR);
        buttons.add(buttonS);
        buttons.add(buttonŚ);
        buttons.add(buttonT);
        buttons.add(buttonU);
        buttons.add(buttonW);
        buttons.add(buttonY);
        buttons.add(buttonZ);
        buttons.add(buttonŹ);
        buttons.add(buttonŻ);
        NetworkManager.getStage().setScene(new Scene(anchorPane));
        wordLabel.setText(NetworkManager.getRound().getHiddenWord());
        Thread moveToWaitingWindow = new Thread(this::run);
        moveToWaitingWindow.start();
    }

    private void run() {
        synchronized (Integer.TYPE) {
            try {
                Integer.TYPE.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (NetworkManager.getRound().getHiddenLettersCounter() != 0) {
            Platform.runLater(() -> {
                for (Button but : buttons) {
                    but.setDisable(true);
                }
                wordLabel.setTextFill(Paint.valueOf("red"));
                wordLabel.setUnderline(true);
            });
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            try {
                FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("waitingWindow.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
