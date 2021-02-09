package io.swapastack.gomoku.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.swapastack.gomoku.shared.*;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/**
 * This is the reference implementation of the gomoku game history server.
 * Note:
 *      1. This server implements the network standard
 *      2. This server is super strict with not supported / malformed messages (except HistoryPush).
 *      If a message is not supported or malformed the connection to the client gets closed.
 *      3. There are jUnit unit tests for this server located at /gomoku/server/test/
 *      4. Feel free to harden the implementation of this reference server if you find some edge
 *      cases that are not or not correclty handled.
 *      5. This server only does a simple validation of the userId UUId. The server generates a UUID
 *      after receiving the HelloServer message and stores it. If the client sents further messages
 *      the UUID in the message is checked against a List with valid UUIDs. So the validation is not
 *      that secure, feel free to improve this.
 *      6. The server does not save the history to disc, so if you want to clear the history you just
 *      have to restart the server. The main reason for this is "testing" while developing the client.
 *      If you want to add the saving functionality to this server, then feel free, there is a TODO:
 *      in the code.
 *
 * @author Dennis Jehle
 */
public class GomokuServer extends WebSocketServer {

    // hostname / ip to bind
    // e.g. localhost
    // e.g. 127.0.0.1
    public static final String host = "localhost";
    // port to listen on
    // see: https://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers
    public static final int port = 42000;
    // 'Google Gson is a java library that can be used to convert Java Object
    // into their JSON representation.'
    // see: https://github.com/google/gson
    // see: https://github.com/google/gson/blob/master/UserGuide.md#TOC-Serializing-and-Deserializing-Generic-Types
    private final Gson gson_;

    // This ArrayList stores the UUIDs generated after receiving the HelloServer message.
    // They are used to "validate" if the messages sent by a connected client are legal.
    private ArrayList<UUID> legal_user_ids_;

    // The History store
    private ArrayList<History> history_store_;

    /**
     * io.swapastack.gomoku.server.GomokuServer main method.
     *
     * @param args command line arguments
     * @author Dennis Jehle
     */
    public static void main(String[] args) {
        // create WebSocketServer
        final WebSocketServer server = new GomokuServer(new InetSocketAddress(host, port));
        // see: https://github.com/TooTallNate/Java-WebSocket/wiki/Enable-SO_REUSEADDR
        server.setReuseAddr(true);
        // see: https://github.com/TooTallNate/Java-WebSocket/wiki/Enable-TCP_NODELAY
        server.setTcpNoDelay(true);
        // start the WebSocketServer
        server.start();

        // create ShutdownHook to catch CTRL+C and shutdown server peacefully
        // see: https://docs.oracle.com/javase/8/docs/technotes/guides/lang/hook-design.html
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("ShutdownHook executed.");
                    Thread.sleep(500);
                    System.out.println("Application shutting down.");
                    // shutdown server
                    server.stop();
                } catch (InterruptedException ie) {
                    System.out.printf("InterruptedException: %s", ie);
                    Thread.currentThread().interrupt();
                } catch (IOException ioe) {
                    System.out.printf("IOException: %s", ioe);
                }
            }
        });
    }

    /**
     * The constructor of the io.swapastack.gomoku.server.GomokuServer class.
     *
     * @param address the {@link InetSocketAddress} the server should listen on
     * @author Dennis Jehle
     */
    public GomokuServer(InetSocketAddress address) {
        // https://stackoverflow.com/a/3767389/5380008
        super(address);
        // create Gson converter
        gson_ = new Gson();
        legal_user_ids_ = new ArrayList<UUID>();
        history_store_ = new ArrayList<History>();
        history_store_.add(new History("Player 1","Player 2",true,false));
        history_store_.add(new History("Player 3","Player 4",true,false));
        history_store_.add(new History("Player 5","Player 6",true,false));
        history_store_.add(new History("Player 7","Player 8",true,false));
        history_store_.add(new History("Player 9","Player 10",true,false));
        history_store_.add(new History("Player 1","Player 2",true,false));

    }

    /**
     * This method is called if a new client connected.
     *
     * @param conn {@link WebSocket}
     * @param handshake {@link ClientHandshake}
     * @author Dennis Jehle
     */
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // 'debug' output
        System.out.println("new connection to " + conn.getRemoteSocketAddress());
    }

    /**
     * This method is called if a WebSocket connection was closed.
     *
     * @param conn {@link WebSocket}
     * @param code status code
     * @param reason String containing closing reason
     * @param remote close was initiated by remote client
     * @author Dennis Jehle
     */
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
    }

    /**
     * This method is called if a String message was received.
     * e.g. connected client sends a JSON encoded String message
     *
     * @param conn {@link WebSocket}
     * @param message the String message, e.g. JSON String
     * @author Dennis Jehle
     */
    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("received message: " + message);
        try {
            ExtractorMessage extractorMessage = gson_.fromJson(message, ExtractorMessage.class);
            switch (extractorMessage.messageType) {
                case HelloServer:
                    UUID userId = UUID.randomUUID();
                    legal_user_ids_.add(userId);
                    WelcomeClient welcome_client = new WelcomeClient(userId, "Moin!");
                    String welcome_client_json = gson_.toJson(welcome_client);
                    conn.send(welcome_client_json);
                    break;
                case HistoryPush:
                    HistoryPush history_push = gson_.fromJson(message, HistoryPush.class);
                    if (!legal_user_ids_.contains(history_push.userId)) {
                        conn.close(); // see class description
                        break;
                    }
                    String playerOneName = history_push.playerOneName;
                    String playerTwoName = history_push.playerTwoName;
                    Boolean playerOneWinner = history_push.playerOneWinner;
                    Boolean playerTwoWinner = history_push.playerTwoWinner;
                    if (playerOneName.equals("")
                            || playerOneName == null
                            || playerTwoName.equals("")
                            || playerTwoName == null
                            || (playerOneWinner && playerTwoWinner)
                    ) {
                        HistoryNotSaved history_not_saved = new HistoryNotSaved();
                        String history_not_saved_json = gson_.toJson(history_not_saved);
                        conn.send(history_not_saved_json);
                    } else {
                        history_store_.add(new History(
                           playerOneName,
                           playerTwoName,
                           playerOneWinner,
                           playerTwoWinner
                        ));
                        // TODO: here the history should be actually saved to disc
                        HistorySaved history_saved = new HistorySaved();
                        String history_saved_json = gson_.toJson(history_saved);
                        conn.send(history_saved_json);
                    }
                    break;
                case HistoryGetAll:
                    HistoryGetAll history_get_all = gson_.fromJson(message, HistoryGetAll.class);
                    if (!legal_user_ids_.contains(history_get_all.userId)) {
                        conn.close(); // see class description
                        break;
                    }
                    HistoryAll history_all = new HistoryAll();
                    for (History h : history_store_) {
                        history_all.appendEntry(h.playerOneName, h.playerTwoName, h.playerOneWinner, h.playerTwoWinner);
                    }
                    String history_all_json = gson_.toJson(history_all);
                    conn.send(history_all_json);
                    break;
                case GoodbyeServer:
                    GoodbyeServer goodbye_server = gson_.fromJson(message, GoodbyeServer.class);
                    if (!legal_user_ids_.contains(goodbye_server.userId)) {
                        conn.close(); // see class description
                        break;

                    }
                    GoodbyeClient goodbye_client = new GoodbyeClient("Servus!");
                    String goodbye_client_json = gson_.toJson(goodbye_client);
                    conn.send(goodbye_client_json);
                    conn.close(); // see network standard
                    break;
                default:
                    conn.close(); // see class description
            }
        }
        catch (JsonSyntaxException jse) {
            conn.close(); // see class description
        }
    }

    /**
     * This method is called if a binary message was received.
     * note: this method is not necessary for this project, because
     * the network standard document specifies a JSON String message protocol
     *
     * @param conn {@link WebSocket}
     * @param message the binary message
     * @author Dennis Jehle
     */
    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        // do nothing, because binary messages are not supported
    }

    /**
     * This method is called if an exception was thrown.
     *
     * @param conn {@link WebSocket}
     * @param exception {@link Exception}
     * @author Dennis Jehle
     */
    @Override
    public void onError(WebSocket conn, Exception exception) {
        System.err.println("an error occurred on connection " + conn.getRemoteSocketAddress()  + ":" + exception);
    }

    /**
     * This method is called if the server started successfully.
     *
     * @author Dennis Jehle
     */
    @Override
    public void onStart() {
        System.out.println("Gomoku server started successfully.");
    }
}
