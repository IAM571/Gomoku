package io.swapastack.gomoku.server;

import io.swapastack.gomoku.shared.*;
import org.java_websocket.server.WebSocketServer;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameHistoryServerTests {

    public static final int DELAY_TIME_MILLISECONDS = 200; 

    @Test
    public void alwaysTrue() {
        assertEquals(42, 42);
    }

    /**
     * This test is used to check if the WebSocket connection could be established between client and server.
     * Since the WebSocket framework uses non-blocking methods, there is a Thread.sleep() call in this test method.
     *
     * @throws IOException
     * @throws InterruptedException
     * @throws URISyntaxException
     *
     * @author Dennis Jehle
     */
    @Test
    public void startServerAndConnect() throws IOException, InterruptedException, URISyntaxException {

        String hostname = "localhost";
        int port = 42001;
        URI server_uri = new URI(String.format("ws://%s:%d", hostname, port));

        final WebSocketServer server = new GomokuServer(new InetSocketAddress(hostname, port));
        server.setReuseAddr(true);
        server.setTcpNoDelay(true);
        server.start();

        Thread.sleep(DELAY_TIME_MILLISECONDS);
        TestClient test_client = new TestClient(server_uri);
        test_client.connect();
        Thread.sleep(DELAY_TIME_MILLISECONDS);

        assertEquals(true, test_client.connection_opened);

        test_client.close();

        server.stop();

    }

    /**
     * This test is used to check if the server send a WelcomeClient message after receiving the HelloServer
     * message as specified in the network standard.
     * This test also checks if the UUID is not null and the welcome message is "Moin!".
     *
     * @throws URISyntaxException
     * @throws InterruptedException
     * @throws IOException
     *
     * @author Dennis Jehle
     */
    @Test
    public void sendHelloServerMessage() throws URISyntaxException, InterruptedException, IOException {

        Gson gson = new Gson();

        String hostname = "localhost";
        int port = 42002;
        URI server_uri = new URI(String.format("ws://%s:%d", hostname, port));

        final WebSocketServer server = new GomokuServer(new InetSocketAddress(hostname, port));
        server.setReuseAddr(true);
        server.setTcpNoDelay(true);
        server.start();

        Thread.sleep(DELAY_TIME_MILLISECONDS);
        TestClient test_client = new TestClient(server_uri);
        test_client.connect();
        Thread.sleep(DELAY_TIME_MILLISECONDS);

        assertEquals(true, test_client.connection_opened);

        String hello_server_json = "{messageType:\"HelloServer\"}";

        test_client.send(hello_server_json);

        Thread.sleep(DELAY_TIME_MILLISECONDS);

        assert(0 != test_client.messages_received.size());

        String answer = test_client.messages_received.poll();

        WelcomeClient welcome_client = gson.fromJson(answer, WelcomeClient.class);

        assert(null != welcome_client.userId);
        assert(welcome_client.welcomeMessage.equals("Moin!"));

        test_client.close();

        server.stop();

    }

    /**
     * This test is used to check if the server handles a valid HistoryPush message correctly.
     *
     * @throws URISyntaxException
     * @throws InterruptedException
     * @throws IOException
     *
     * @author Dennis Jehle
     */
    @Test
    public void sendValidHistoryPushMessage() throws URISyntaxException, InterruptedException, IOException {

        Gson gson = new Gson();

        String hostname = "localhost";
        int port = 42003;
        URI server_uri = new URI(String.format("ws://%s:%d", hostname, port));

        final WebSocketServer server = new GomokuServer(new InetSocketAddress(hostname, port));
        server.setReuseAddr(true);
        server.setTcpNoDelay(true);
        server.start();

        Thread.sleep(DELAY_TIME_MILLISECONDS);
        TestClient test_client = new TestClient(server_uri);
        test_client.connect();
        Thread.sleep(DELAY_TIME_MILLISECONDS);

        assertEquals(true, test_client.connection_opened);

        String hello_server_json = "{messageType:\"HelloServer\"}";

        test_client.send(hello_server_json);

        Thread.sleep(DELAY_TIME_MILLISECONDS);

        assert(1 == test_client.messages_received.size());

        String answer = test_client.messages_received.poll();

        WelcomeClient welcome_client = gson.fromJson(answer, WelcomeClient.class);

        assert(null != welcome_client.userId);

        UUID userId = welcome_client.userId;

        HistoryPush history_push = new HistoryPush(userId, "Hubert", "Helga", false, true);
        String history_push_json = gson.toJson(history_push);

        test_client.send(history_push_json);

        Thread.sleep(DELAY_TIME_MILLISECONDS);

        assert(1 == test_client.messages_received.size());

        HistorySaved history_saved = gson.fromJson(test_client.messages_received.poll(), HistorySaved.class);

        assert(history_saved.messageType == MessageType.HistorySaved);

        test_client.close();

        server.stop();

    }

    /**
     * This test is used to check if the server handles a invalid HistoryPush message correctly.
     *
     * @throws URISyntaxException
     * @throws InterruptedException
     * @throws IOException
     *
     * @author Dennis Jehle
     */
    @Test
    public void sendInvalidHistoryPushMessage() throws URISyntaxException, InterruptedException, IOException {

        Gson gson = new Gson();

        String hostname = "localhost";
        int port = 42004;
        URI server_uri = new URI(String.format("ws://%s:%d", hostname, port));

        final WebSocketServer server = new GomokuServer(new InetSocketAddress(hostname, port));
        server.setReuseAddr(true);
        server.setTcpNoDelay(true);
        server.start();

        Thread.sleep(DELAY_TIME_MILLISECONDS);
        TestClient test_client = new TestClient(server_uri);
        test_client.connect();
        Thread.sleep(DELAY_TIME_MILLISECONDS);

        assertEquals(true, test_client.connection_opened);

        String hello_server_json = "{messageType:\"HelloServer\"}";

        test_client.send(hello_server_json);

        Thread.sleep(DELAY_TIME_MILLISECONDS);

        assert(1 == test_client.messages_received.size());

        String answer = test_client.messages_received.poll();

        WelcomeClient welcome_client = gson.fromJson(answer, WelcomeClient.class);

        assert(null != welcome_client.userId);

        UUID userId = welcome_client.userId;

        // invalid
        HistoryPush history_push = new HistoryPush(userId, "Hubert", "Helga", true, true);
        String history_push_json = gson.toJson(history_push);

        test_client.send(history_push_json);

        Thread.sleep(DELAY_TIME_MILLISECONDS);

        assert(1 == test_client.messages_received.size());

        HistoryNotSaved history_not_saved = gson.fromJson(test_client.messages_received.poll(), HistoryNotSaved.class);

        assert(history_not_saved.messageType == MessageType.HistoryNotSaved);

        test_client.close();

        server.stop();

    }

    /**
     * This test is used to check that the client can send more than one HistoryPush message to the server.
     * This is specified in the network standard.
     *
     * @throws URISyntaxException
     * @throws InterruptedException
     * @throws IOException
     *
     * @author Dennis Jehle
     */
    @Test
    public void sendTwoHistoryPushMessages() throws URISyntaxException, InterruptedException, IOException {

        Gson gson = new Gson();

        String hostname = "localhost";
        int port = 42005;
        URI server_uri = new URI(String.format("ws://%s:%d", hostname, port));

        final WebSocketServer server = new GomokuServer(new InetSocketAddress(hostname, port));
        server.setReuseAddr(true);
        server.setTcpNoDelay(true);
        server.start();

        Thread.sleep(DELAY_TIME_MILLISECONDS);
        TestClient test_client = new TestClient(server_uri);
        test_client.connect();
        Thread.sleep(DELAY_TIME_MILLISECONDS);

        assertEquals(true, test_client.connection_opened);

        String hello_server_json = "{messageType:\"HelloServer\"}";

        test_client.send(hello_server_json);

        Thread.sleep(500);

        assert(1 == test_client.messages_received.size());

        String answer = test_client.messages_received.poll();

        WelcomeClient welcome_client = gson.fromJson(answer, WelcomeClient.class);

        assert(null != welcome_client.userId);

        UUID userId = welcome_client.userId;

        HistoryPush history_push_1 = new HistoryPush(userId, "Hubert", "Helga", true, false);
        String history_push_1_json = gson.toJson(history_push_1);
        test_client.send(history_push_1_json);

        Thread.sleep(DELAY_TIME_MILLISECONDS);

        HistoryPush history_push_2 = new HistoryPush(userId, "Anton", "Anna", false, true);
        String history_push_2_json = gson.toJson(history_push_2);
        test_client.send(history_push_2_json);

        Thread.sleep(DELAY_TIME_MILLISECONDS);

        assert(2 == test_client.messages_received.size());

        HistorySaved history_1_saved = gson.fromJson(test_client.messages_received.poll(), HistorySaved.class);
        assert(history_1_saved.messageType == MessageType.HistorySaved);

        HistorySaved history_2_saved = gson.fromJson(test_client.messages_received.poll(), HistorySaved.class);
        assert(history_2_saved.messageType == MessageType.HistorySaved);

        test_client.close();

        server.stop();

    }

    /**
     * This test is used to check that the HistoryGetAll message is handled correctly within the server.
     * The test sends two valid HistoryPush messages to the server and a HistoryGetAll message.
     * So the HistoryAll message should contain the two History entries.
     *
     * @throws URISyntaxException
     * @throws InterruptedException
     * @throws IOException
     *
     * @author Dennis Jehle
     */
    @Test
    public void sendTwoHistoryPushMessagesAndGetAll() throws URISyntaxException, InterruptedException, IOException {

        Gson gson = new Gson();

        String hostname = "localhost";
        int port = 42006;
        URI server_uri = new URI(String.format("ws://%s:%d", hostname, port));

        final WebSocketServer server = new GomokuServer(new InetSocketAddress(hostname, port));
        server.setReuseAddr(true);
        server.setTcpNoDelay(true);
        server.start();

        Thread.sleep(DELAY_TIME_MILLISECONDS);

        TestClient test_client = new TestClient(server_uri);
        test_client.connect();

        Thread.sleep(DELAY_TIME_MILLISECONDS);

        assertEquals(true, test_client.connection_opened);

        String hello_server_json = "{messageType:\"HelloServer\"}";

        test_client.send(hello_server_json);

        Thread.sleep(DELAY_TIME_MILLISECONDS);

        assert(1 == test_client.messages_received.size());

        String answer = test_client.messages_received.poll();

        WelcomeClient welcome_client = gson.fromJson(answer, WelcomeClient.class);

        assert(null != welcome_client.userId);

        UUID userId = welcome_client.userId;

        HistoryPush history_push_1 = new HistoryPush(userId, "Hubert", "Helga", true, false);
        String history_push_1_json = gson.toJson(history_push_1);
        test_client.send(history_push_1_json);

        Thread.sleep(DELAY_TIME_MILLISECONDS);

        HistoryPush history_push_2 = new HistoryPush(userId, "Anton", "Anna", false, true);
        String history_push_2_json = gson.toJson(history_push_2);
        test_client.send(history_push_2_json);

        Thread.sleep(DELAY_TIME_MILLISECONDS);

        assert(2 == test_client.messages_received.size());

        HistorySaved history_1_saved = gson.fromJson(test_client.messages_received.poll(), HistorySaved.class);
        assert(history_1_saved.messageType == MessageType.HistorySaved);

        HistorySaved history_2_saved = gson.fromJson(test_client.messages_received.poll(), HistorySaved.class);
        assert(history_2_saved.messageType == MessageType.HistorySaved);

        HistoryGetAll history_get_all = new HistoryGetAll(userId);
        String history_get_all_json = gson.toJson(history_get_all);
        test_client.send(history_get_all_json);

        Thread.sleep(DELAY_TIME_MILLISECONDS);

        assert(1 == test_client.messages_received.size());

        HistoryAll history_all = gson.fromJson(test_client.messages_received.poll(), HistoryAll.class);

        assert(history_all.history.get(0).playerOneName.equals("Hubert"));
        assert(history_all.history.get(0).playerTwoName.equals("Helga"));
        assert(history_all.history.get(0).playerOneWinner == true);
        assert(history_all.history.get(0).playerTwoWinner == false);

        assert(history_all.history.get(1).playerOneName.equals("Anton"));
        assert(history_all.history.get(1).playerTwoName.equals("Anna"));
        assert(history_all.history.get(1).playerOneWinner == false);
        assert(history_all.history.get(1).playerTwoWinner == true);

        test_client.close();

        server.stop();

    }

    /**
     * This test is used to check if the server handles the GoodbyeServer message correctly.
     *
     * @throws URISyntaxException
     * @throws InterruptedException
     * @throws IOException
     *
     * @author Dennis Jehle
     */
    @Test
    public void sendGoodbyeServerMessage() throws URISyntaxException, InterruptedException, IOException {

        Gson gson = new Gson();

        String hostname = "localhost";
        int port = 42007;
        URI server_uri = new URI(String.format("ws://%s:%d", hostname, port));

        final WebSocketServer server = new GomokuServer(new InetSocketAddress(hostname, port));
        server.setReuseAddr(true);
        server.setTcpNoDelay(true);
        server.start();

        Thread.sleep(DELAY_TIME_MILLISECONDS);
        TestClient test_client = new TestClient(server_uri);
        test_client.connect();
        Thread.sleep(DELAY_TIME_MILLISECONDS);


        assertEquals(true, test_client.connection_opened);

        String hello_server_json = "{messageType:\"HelloServer\"}";

        test_client.send(hello_server_json);

        Thread.sleep(DELAY_TIME_MILLISECONDS);

        assert(1 == test_client.messages_received.size());

        String answer = test_client.messages_received.poll();

        WelcomeClient welcome_client = gson.fromJson(answer, WelcomeClient.class);

        assert(null != welcome_client.userId);

        UUID userId = welcome_client.userId;

        GoodbyeServer goodbye_server = new GoodbyeServer(userId);
        String goodbye_server_json = gson.toJson(goodbye_server);
        test_client.send(goodbye_server_json);

        Thread.sleep(DELAY_TIME_MILLISECONDS);

        assert(1 == test_client.messages_received.size());

        GoodbyeClient goodbyeClient = gson.fromJson(test_client.messages_received.poll(), GoodbyeClient.class);
        assert(goodbyeClient.goodbyeMessage.equals("Servus!"));

        assert(test_client.connection_closed == true);

        test_client.close();

        server.stop();

    }
}
