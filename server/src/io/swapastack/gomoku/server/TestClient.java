package io.swapastack.gomoku.server;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * This io.swapastack.gomoku.server.TestClient class is used for the game history servers unit tests.
 * Do not use this WebSocketClient implementation in production.
 *
 * @author Dennis Jehle
 */
public class TestClient extends WebSocketClient {

    Queue<String> messages_received = new ArrayDeque<String>();

    boolean connection_opened = false;
    boolean connection_closed = false;
    boolean exception_occured = false;

    public TestClient(URI server_uri) {
        super(server_uri);
        this.setTcpNoDelay( true );
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        connection_opened = true;
    }

    @Override
    public void onMessage(String message) {
        messages_received.add(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        connection_closed = true;
    }

    @Override
    public void onError(Exception ex) {
        exception_occured = true;
    }

}
