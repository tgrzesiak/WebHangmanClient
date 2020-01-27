package org.spacesloth;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.spacesloth.logic.NetworkManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

public class Main extends Application {

    public static void main(String[] args) {
        if (args.length == 2) {
            NetworkManager.setHost(args[0]);
            NetworkManager.setPort(Integer.parseInt(args[1]));
        } else {
            try {
                System.out.println(System.getProperty("user.dir"));
                Scanner fs = new Scanner(new FileInputStream("src/main/resources/config"));
                NetworkManager.setHost(fs.next());
                NetworkManager.setPort(fs.nextInt());
            } catch (FileNotFoundException e) {
                System.out.println("Brak pliku 'config'");
                System.exit(0);
            }
        }
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("openingWindow.fxml")));
    }
}
