package io.swapastack.gomoku.shared;

import java.util.UUID;

/**
 * This class represents the WelcomeClient message specified in the network standard.
 * This message is used to assign a connection specific UUID to the client.
 * This UUID is used to identify the client.
 * The welcomeMessage String is not strictly specified in the network standard, so it could be null empty or string.
 *
 * @author Dennis Jehle
 */
public class WelcomeClient {
    MessageType messageType = MessageType.WelcomeClient;
    public UUID userId;
    public String welcomeMessage;

    public WelcomeClient(UUID userId, String welcomeMessage) {
        this.userId = userId;
        this.welcomeMessage = welcomeMessage;
    }
}
