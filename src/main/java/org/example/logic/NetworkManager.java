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
    private volatile static int playersCounter;
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

    public static void setPlayerState(PlayerState playerState) { NetworkManager.playerState = playerState; }

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

    public static void sendSyncSignal() {
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

    private static void readScores() {
        String data = scanner.next();
        while(!data.equals("end")) {
            System.out.println(data);
            String[] values = data.split("-");
            Integer[] tmp = round.getOtherScores();
            tmp[Integer.parseInt(values[1])] = Integer.parseInt(values[0]);
            round.setOtherScores(tmp);
            sendSyncSignal();
            data = scanner.next();
            //TODO na serwerze błędny format - dwa myślniki czasem
        }
    }

    public static void listenToNetwork() {
        if (!scanner.hasNextInt()) confirmString("wait");
        queueTimeout = scanner.nextInt();
        playerState = PlayerState.IN_QUEUE;
        while(true) {
            countdown = scanner.nextInt();
            //TODO co robi klient i serwer kiedy jest jeden gracz?
            playerState = PlayerState.COUNTDOWN;
            sendSyncSignal();

            Timer countdownTimer = new Timer();
            countdownTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (countdown-- >= 0) sendSyncSignal();
                }
            }, 0, 1000);

            playersCounter = scanner.nextInt();
            System.out.println("playerCounter: "+ playersCounter);
            int roundNumber = scanner.nextInt();
            System.out.println("roundNumber: "+roundNumber);
            String word = scanner.next();

            countdownTimer.cancel();
            if (!word.equals(word.toUpperCase())) {
                System.out.println("Wystąpił błąd, przepraszamy...");
                System.exit(0);
            }

            round = new Round(word, roundNumber, playersCounter);
            playerState = PlayerState.ROUND;
            sendSyncSignal();

            readScores();
            playerState = PlayerState.END_OF_ROUND;
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
