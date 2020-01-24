package org.example.controllers;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.example.logic.PlayerState;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static org.example.logic.NetworkManager.*;

public class GameWindowController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label categoryLabel;

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
    private ListView<String> scoresListView;

    @FXML
    private Label textLabel;

    @FXML
    private Label livesLabel;

    @FXML
    private Label textLabel1;

    @FXML
    private Label roundLabel;

    @FXML
    private void newWord(MouseEvent event) {
        timer.cancel();
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
        int diffrence = getRound().updateHiddenWord(button.getText().toCharArray()[0]);
        wordLabel.setText(getRound().getHiddenWord());
        if (getRound().getHiddenLettersCounter() == 0) {
            for (Button but : buttons) {
                but.setDisable(true);
            }
            wordLabel.setTextFill(Paint.valueOf("green"));
            wordLabel.setUnderline(true);
        }
        else button.setDisable(true);
        if (diffrence == 0) {
            updateLives();
        }
        setTimer();
    }


    private ArrayList<Button> buttons;
    private Timer timer;

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
        getStage().setScene(new Scene(anchorPane));
        categoryLabel.setText(getRound().getCategory());
        wordLabel.setText(getRound().getHiddenWord());
        Thread moveToWaitingWindow = new Thread(this::run);
        moveToWaitingWindow.start();
        scoresListView.setItems(FXCollections.observableArrayList(getRound().getOtherNamesScores()));
        livesLabel.setText(String.valueOf(getRound().getLives()));
        roundLabel.setText(String.valueOf(getRound().getRoundNumber()));
        setTimer();
    }

    private void setTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateLives();
            }
        }, getRound().getButtonDelay(), getRound().getButtonDelay());
    }

    private void updateLives() {
        int lives = getRound().getLives();
        if (lives > 0) {
            getRound().setLives(--lives);
            Platform.runLater(() -> livesLabel.setText(String.valueOf(getRound().getLives())));
        }
        if (lives == 0) {
            setPlayerState(PlayerState.END_OF_ROUND);
            sendSyncSignal();
        }
    }

    private void run() {
        while (true) {
            synchronized (Integer.TYPE) {
                try {
                    Integer.TYPE.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (getPlayerState() == PlayerState.ROUND) {
                Platform.runLater(this::updateScores);
                continue;
            }
            if (getPlayerState() == PlayerState.END_OF_ROUND) {
                if (getRound().getHiddenLettersCounter() != 0) {
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
    }

    private void updateScores() {
        scoresListView.setItems(FXCollections.observableArrayList(getRound().getOtherNamesScores()));
    }
}
