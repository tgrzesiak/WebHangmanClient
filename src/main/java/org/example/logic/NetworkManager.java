package org.example.logic;

import javafx.stage.Stage;

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
    private static Scanner scanner;

    private static PlayerState playerState = PlayerState.WAIT_FOR_BEGINNING;
    private volatile static int queueTimeout;
    private volatile static int countdown;
    private volatile static int playerCounter;
    private static int TIMEOUT = 10;

    private static Stage stage;
    private static Round round;

    public static OutputStream getSocketOS() { return NetworkManager.socketOS; }

    public static Round getRound() {
        return NetworkManager.round;
    }

    public static int getCountdown() {
        return NetworkManager.countdown;
    }

    public static int getQueueTimeout() {
        return NetworkManager.queueTimeout;
    }

    public static Stage getStage() {
        return NetworkManager.stage;
    }

    public static void setStage(Stage stage) {
        NetworkManager.stage = stage;
    }

    public static void setHost(String host) {
        NetworkManager.host = host;
    }

    public static PlayerState getPlayerState() {
        return NetworkManager.playerState;
    }

    public static void setPort(int port) {
        NetworkManager.port = port;
    }


    public static void connect() {
        try {
            Socket socket = new Socket(host, port);
            socketIS = socket.getInputStream();
            socketOS = socket.getOutputStream();
            scanner = new Scanner(socketIS);
        } catch (IOException e) {
            System.out.println("Błąd podczas nawiązywania połączenia, przepraszamy za problemy...");
            System.exit(0);
        }
    }

    private static void sendSyncSignal() {
        synchronized (Integer.TYPE) {
            Integer.TYPE.notifyAll();
        }
    }

    private static void confirmString(String str) {
        if (!scanner.next().equals(str)) {
            System.out.println("Błąd przy odczytywaniu: " + str);
            System.exit(0);
        }
        System.out.println(str);
    }

    public static void listenToNetwork() {
        /*while (playerState == PlayerState.WAIT_FOR_BEGINNING) {
            confirmString("welcome");
            if (scanner.hasNextInt()) {
                queueTimeout = TIMEOUT - scanner.nextInt();
                System.out.println("queueTimeout: "+queueTimeout);
                playerState = PlayerState.IN_QUEUE;
                sendSyncSignal();
            } else {
                confirmString("wait");
                playerState = PlayerState.WAIT_FOR_BEGINNING;
            }
        }*/
        if (!scanner.hasNextInt()) confirmString("wait");
        queueTimeout = scanner.nextInt();
        playerState = PlayerState.IN_QUEUE;
        System.out.println("Odliczanie rozpocznie się za około " + queueTimeout + " sekund");
        while(true) {
            countdown = scanner.nextInt();
            System.out.println("countdown: "+countdown);
            //TODO co robi klient i serwer kiedy jest jeden gracz?
            playerState = PlayerState.COUNTDOWN;
            sendSyncSignal();

            //odliczanie od `countdown` do 0
            Timer countdownTimer = new Timer();
            countdownTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (countdown-- >= 0) sendSyncSignal();
                }
            }, 0, 1000);

            playerCounter = scanner.nextInt();
            System.out.println("playerCounter: "+playerCounter);
            int roundNumber = scanner.nextInt();
            System.out.println("roundNumber: "+roundNumber);
            String word = scanner.next();
            System.out.println("word: "+word);

            //anulowanie timera synchornizowane przysłaniem kolejnych danych z serwera
            countdownTimer.cancel();
            if (!word.equals(word.toUpperCase())) {
                System.out.println("Wystąpił błąd, przepraszamy...");
                System.exit(0);
            }

            round = new Round(word);
            playerState = PlayerState.ROUND;
            sendSyncSignal();

            confirmString("end");
            sendSyncSignal();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (round.getHiddenLettersCounter() == 0) playerState = PlayerState.IN_QUEUE;
            else playerState = PlayerState.WAIT_FOR_BEGINNING;
            sendSyncSignal();

            System.out.println("Nowa runda rozpocznie się za chwilę...");
        }
    }
}
