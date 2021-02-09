package io.swapastack.gomoku.shared;

import java.util.UUID;

/**
 * This class represents the GoodbyeServer message specified in the network standard.
 * This message is used to tell the server, that the connection is no longer needed and gets closed soon.
 * This message must contain the connection specific userId as UUID.
 *
 * @author Dennis Jehle
 */
public class GoodbyeServer {
    public MessageType messageType = MessageType.GoodbyeServer;
    public UUID userId;

    public GoodbyeServer(UUID userId) {
        this.userId = userId;
    }
}
