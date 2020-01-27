package org.spacesloth.logic;

import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

    public static void exitGame() {
        disconnect();
        System.out.println("Dziękujemy za grę :)");
        System.exit(0);
    }

    public static void disconnect() {
        sendInt(-1);
    }

    public static void sendSyncSignal() {
        synchronized (Integer.TYPE) {
            Integer.TYPE.notifyAll();
        }
    }

    public static void sendInt(int number) {
        try {
            for (byte b : String.valueOf(number).getBytes(StandardCharsets.UTF_8))
                System.out.println(b);
            socketOS.write(String.valueOf(number).getBytes(StandardCharsets.UTF_8));
            System.out.println("Sent: "+number);
        } catch (IOException e) {
            System.out.println("Nieudane wysyłanie numeru (" + number + ")");
            //System.exit(0);
        }
    }

    private static int readInt(String name) {
        if (scanner.hasNextInt()) {
            int smh = scanner.nextInt();
            System.out.println(name+": "+smh);
            return smh;
        }
        else {
            System.out.println("Nieudane odczytanie numeru (" + name + ")");
            //exitGame();
            return -1;
        }
    }

    private static String readString(String name) {
        if (scanner.hasNext()) {
            String smh = scanner.next();
            System.out.println(name+": "+smh);
            return smh;
        }
        else {
            System.out.println("Nieudane odczytanie słowa (" + name + ")");
            //exitGame();
            return "";
        }
    }

    private static void confirmString(String str) {
        if (!readString(str).equals(str)) {
            System.out.println("Odczytano błędne słowo (oczekiwane: " + str + ")");
            //exitGame();
        }
    }

    private static void readScores() {
        while (true) {
            String data = readString("fd:score");
            if (data.equals("end") | data.equals("")) break;
            System.out.println(data);
            String[] values = data.split("-");
            String[] tmp = round.getOtherNamesScores();
            int index = round.matchIndex(Integer.parseInt(values[1]));
            tmp[index] = tmp[index].substring(0, 8) + values[0];
            round.setOtherNamesScores(tmp);
            sendSyncSignal();
        }
    }

    public static void listenToNetwork() {
        if (!scanner.hasNextInt()) confirmString("wait");
        else {
            queueTimeout = readInt("queueTimeout");
            playerState = PlayerState.IN_QUEUE;
        }
        while(true) {
            if (!scanner.hasNextInt()) {
                confirmString("win");
                playerState = PlayerState.WINNER;
                sendSyncSignal();
            }
            countdown = readInt("countdown");
            playerState = PlayerState.COUNTDOWN;
            sendSyncSignal();

            Timer countdownTimer = new Timer();
            countdownTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (countdown-- > 0){
                        sendSyncSignal();
                    }
                }
            }, 0, 1000);

            playersCounter = readInt("playersCounter");
            System.out.println("playerCounter: "+ playersCounter);
            if (playersCounter == 1) {
                playerState = PlayerState.TOO_FEW_PLAYERS;
                countdownTimer.cancel();
                sendSyncSignal();
                continue;
            }
            int roundNumber = readInt("roundNumber");
            System.out.println("roundNumber: "+roundNumber);
            String word = readString("word");

            countdownTimer.cancel();
            if (!word.equals(word.toUpperCase())) {
                System.out.println("Wystąpił błąd, przepraszamy...");
                System.exit(0);
            }

            round = new Round(word, roundNumber, playersCounter);
            playerState = PlayerState.ROUND;
            sendSyncSignal();

            readScores();
            if (playerState == PlayerState.LOSER) {
                System.out.println("przerwano");
                break;
            }

            System.out.println("Nowa runda rozpocznie się za chwilę...");
        }
    }
}
