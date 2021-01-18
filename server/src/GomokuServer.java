import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import com.google.gson.Gson;
import io.swapastack.gomoku.shared.TestMessage;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

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

    /**
     * GomokuServer main method.
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
     * The constructor of the GomokuServer class.
     *
     * @param address the {@link InetSocketAddress} the server should listen on
     * @author Dennis Jehle
     */
    public GomokuServer(InetSocketAddress address) {
        // https://stackoverflow.com/a/3767389/5380008
        super(address);
        // create Gson converter
        gson_ = new Gson();
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
        // create new TestMassage Java object
        TestMessage message = new TestMessage("GomokuServer", "Welcome to the Gomoku echo server!");
        // create JSON String from TestMessage Java object
        String test_message = gson_.toJson(message);
        // send JSON encoded String message to newly connected client
        conn.send(test_message); //This method sends a message to the new client
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
        System.out.println("received message from "	+ conn.getRemoteSocketAddress() + ": " + message);
        // echo the message back, this is the part why the echo server is called echo server
        conn.send(message);
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
        System.out.println("Gomoku echo server started successfully.");
    }
}
