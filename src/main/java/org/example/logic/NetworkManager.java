package org.example.logic;

import javafx.stage.Stage;
import org.example.controllers.WaitingWindowController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkManager {

    private static String host;
    private static int port;
    private static InputStream socketIS;
    private static OutputStream socketOS;

    private static PlayerState playerState = PlayerState.WAIT_FOR_BEGINNING;
    private volatile static int queueTimeout;
    private volatile static int countdown;
    private volatile static int playerCounter;
    private static int TIMEOUT = 10;

    private static Stage stage;
    private static Round round;

    public static int getPlayerCounter() {
        return playerCounter;
    }

    public static Round getRound() {
        return round;
    }

    public static int getCountdown() {
        return countdown;
    }

    public static int getQueueTimeout() {
        return queueTimeout;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        NetworkManager.stage = stage;
    }

    public static void setHost(String _host) {
        host = _host;
    }

    public static PlayerState getPlayerState() {
        return playerState;
    }

    public static void setPlayerState(PlayerState _playerState) {
        playerState = _playerState;
    }

    public static void setPort(int _port) { port = _port; }

    public static void connect() {
        try {
            Socket socket = new Socket(host, port);
            socketIS = socket.getInputStream();
            socketOS = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println("Błąd podczas nawiązywania połączenia, przepraszamy za problemy...");
            System.exit(0);
        }
    }

    public static void readData() {
        Scanner scanner = new Scanner(socketIS);
        while (playerState == PlayerState.WAIT_FOR_BEGINNING) {
            if (!scanner.next().equals("welcome")) {
                System.out.println("Wystąpił błąd, przepraszamy...");
                System.exit(0);
            }
            if (scanner.hasNextInt()) {
                queueTimeout = TIMEOUT - scanner.nextInt();
                playerState = PlayerState.IN_QUEUE;
                synchronized (Integer.TYPE) {
                    Integer.TYPE.notifyAll();
                }
            } else {
                if (!scanner.next().equals("wait")) {
                    System.out.println("Wystąpił błąd, przepraszamy...");
                    System.exit(0);
                }
                playerState = PlayerState.WAIT_FOR_BEGINNING;
            }
        }
        System.out.println("Odliczanie rozpocznie się za około " + queueTimeout + " sekund");
        while(true) {
            countdown = scanner.nextInt();
            //TODO co robi klient i serwer kiedy jest jeden gracz?
            playerState = PlayerState.COUNTDOWN;
            synchronized (Integer.TYPE) {
                Integer.TYPE.notifyAll();
            }

            //odliczanie od `countdown` do 0
            Timer countdownTimer = new Timer();
            countdownTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (countdown-- >= 0) {
                        synchronized (Integer.TYPE) {
                            Integer.TYPE.notifyAll();
                        }
                    }
                }
            }, 0, 1000);

            final String word = scanner.next();
            playerCounter = scanner.nextInt();

            //anulowanie timera synchornizowane przysłaniem kolejnych danych z serwera
            countdownTimer.cancel();
            if (!word.equals(word.toUpperCase())) {
                System.out.println("Wystąpił błąd, przepraszamy...");
                System.exit(0);
            }

            round = new Round(word);
            playerState = PlayerState.ROUND;
            synchronized (Integer.TYPE) {
                Integer.TYPE.notifyAll();
            }

            if (!scanner.next().equals("end")) {
                System.out.println("Wystąpił błąd end, przepraszamy...");
                System.exit(0);
            }

            if (round.getHiddenLettersCounter() == 0) playerState = PlayerState.COUNTDOWN;
            else playerState = PlayerState.WAIT_FOR_BEGINNING;

            //TODO notify() do odebrania w GameWindowController
            synchronized (Integer.TYPE) {
                Integer.TYPE.notifyAll();
            }

            System.out.println("Nowa runda rozpocznie się za chwilę...");
        }
    }
}
