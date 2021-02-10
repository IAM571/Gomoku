package io.swapastack.gomoku;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.swapastack.gomoku.shared.*;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * The SimpleClient extends the WebSocketClient class.
 * The SimpleClient class could be used to establish a WebSocket (ws, wss) connection
 * a WebSocketServer.
 *
 * The send(String message) method could be used to send a String message to the
 * WebSocketServer.
 *
 * note: this client could be used to implement the network standard document.
 *
 * @author Dennis Jehle & Ibtsam Ali Mahmood
 */
public class SimpleClient extends WebSocketClient {

    // 'Google Gson is a java library that can be used to convert Java Object
    // into their JSON representation.'
    // see: https://github.com/google/gson
    // see: https://github.com/google/gson/blob/master/UserGuide.md#TOC-Serializing-and-Deserializing-Generic-Types
    private final Gson gson_;
    private UUID userID;
    private Map<String, PlayerAndScore> playerAndScoreMap;
    /**
     * This is the constructor of the SimpleClient class.
     *
     * @param server_uri {@link URI}
     * @author Dennis Jehle
     */
    public SimpleClient(URI server_uri) {
        super(server_uri);
        // create Gson converter
        gson_ = new Gson();
    }

    /**
     * This method is called if the connection to the WebSocketServer is open.
     *
     * @param handshake_data {@link ServerHandshake}
     * @author Dennis Jehle
     */
    @Override
    public void onOpen(ServerHandshake handshake_data) {
        // create new TestMassage Java object
        HelloServer message = new HelloServer();
        // create JSON String from TestMessage Java object
        String test_message = gson_.toJson(message);
        // send JSON encoded test message as String to the connected WebSocket server
        send(test_message);
        // 'debug' output
        System.out.println("new connection opened");
    }

    /**
     * This method is called if the connection to the WebSocketServer was closed.
     *
     * @param code status code
     * @param reason the reason for closing the connection
     * @param remote was the close initiated by the remote host
     * @author Dennis Jehle
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("closed with exit code " + code + " additional info: " + reason);
    }

    /**
     * This message is called if the WebSocketServer sends a String message to the client.
     *
     * @param message a String message from the WebSocketServer e.g. JSON message
     * @author Dennis Jehle
     */
    @Override
    public void onMessage(String message) {
        System.out.println("received message: " + message);

        try {
            ExtractorMessage extractorMessage = gson_.fromJson(message, ExtractorMessage.class);
            switch (extractorMessage.messageType) {
                case WelcomeClient:
                    WelcomeClient welcomeClient = gson_.fromJson(message, WelcomeClient.class);
                    userID = welcomeClient.userId;
                    break;
                case HistoryAll:
                    HistoryAll historyAll = gson_.fromJson(message, HistoryAll.class);
                    calculate_scores(historyAll.history);
                    System.out.println(historyAll.history);
                    break;
            }
        }
        catch (JsonSyntaxException jse) {
            close(); // see class description
        }

    }

    /**
     * @param historyAll
     * Hier wird aus den Spielverläufen eine Scoreliste erstellt.
     * Der Spieler und dessen Score wird hier berechnet. Bei einem Sieg erhält man 3 Punkte und bei einer Niederlage
     * keinen. Über die Map werden die zwei Werte eingespeichert.
     */
    private void calculate_scores(ArrayList<History> historyAll){
        Map<String, PlayerAndScore>  scoreMap = new HashMap<>();
        for(History  history : historyAll){
            if (scoreMap.containsKey(history.playerOneName)){
                PlayerAndScore playerAndScore = scoreMap.get(history.playerOneName);
                playerAndScore.setScore(calculate_score(playerAndScore.getScore(),history.playerOneWinner));
                scoreMap.put(history.playerOneName, playerAndScore);
            } else {
                scoreMap.put(history.playerOneName,
                        new PlayerAndScore(history.playerOneName,calculate_score(0,history.playerOneWinner)));
            }
            if (scoreMap.containsKey(history.playerTwoName)){
                PlayerAndScore playerAndScore = scoreMap.get(history.playerTwoName);
                playerAndScore.setScore(calculate_score(playerAndScore.getScore(),history.playerTwoWinner));
                scoreMap.put(history.playerTwoName, playerAndScore);
            } else {
                scoreMap.put(history.playerTwoName,
                        new PlayerAndScore(history.playerTwoName,calculate_score(0,history.playerTwoWinner)));
            }
        }

        playerAndScoreMap= scoreMap;
    }

    private int calculate_score(int score, boolean game){
        if (game){
            score+=3;
        }
        return score;
    }

    /**
     * Die gesamte Spielhistorie wird gesendet.
     * @param playerOneName
     * @param playerTwoName
     * @param playerOneWinner
     * @param playerTwoWinner
     */
    public void send_history_push(String playerOneName,String playerTwoName, boolean playerOneWinner,boolean playerTwoWinner){
        // create new TestMassage Java object
        HistoryPush message = new HistoryPush(userID,playerOneName,playerTwoName,playerOneWinner,playerTwoWinner);
        // create JSON String from TestMessage Java object
        String history_push_message = gson_.toJson(message);
        // send JSON encoded test message as String to the connected WebSocket server
        send(history_push_message);
        // 'debug' output
        System.out.println("Send History Push");
    }

    public void send_history_get_all(){
        // create new TestMassage Java object
        HistoryGetAll message = new HistoryGetAll(userID);
        // create JSON String from TestMessage Java object
        String history_get_all = gson_.toJson(message);
        // send JSON encoded test message as String to the connected WebSocket server
        send(history_get_all);
        // 'debug' output
        System.out.println("Send History get All");
    }
    /**
     * This method is called if the WebSocketServer send a binary message to the client.
     * note: This method is not necessary for this project, because the network standard
     * document specifies a JSON String message protocol.
     *
     * @param message a binary message
     * @author Dennis Jehle
     */
    @Override
    public void onMessage(ByteBuffer message) {
        // do nothing, because binary messages are not supported
    }

    /**
     * This method is called if an exception was thrown.
     *
     * @param exception {@link Exception}
     * @author Dennis Jehle
     */
    @Override
    public void onError(Exception exception) {
        System.err.println("an error occurred:" + exception);
    }

    public Map<String, PlayerAndScore> getPlayerAndScoreMap() {
        return playerAndScoreMap;
    }
}
