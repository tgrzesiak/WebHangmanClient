package org.example.logic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Podaj adres IP i numer portu serwera jako argumenty programu");
            System.exit(0);
        }
        NetworkManager.setHost(args[0]);
        NetworkManager.setPort(Integer.parseInt(args[1]));
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Client side running...");
        FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("openingWindow.fxml")));
    }
}
