package io.swapastack.gomoku.shared;

import java.util.UUID;

/**
 * This class represents the HistoryPush message specified in the network standard.
 * This message is used to add a new game history record to the game history server.
 * This message must contain the connection specific userId as UUID.
 * This message must contain two non-empty && non-null Strings for playerOneName, playerTwoName.
 * This message must contain two boolean values for playerOneWinner, playerTwoWinner, they must not be both true.
 *
 * @author Dennis Jehle
 */
public class HistoryPush {
    public MessageType messageType = MessageType.HistoryPush;
    public UUID userId;
    public String playerOneName;
    public String playerTwoName;
    public Boolean playerOneWinner;
    public Boolean playerTwoWinner;

    public HistoryPush(
            UUID userId
            , String playerOneName
            , String playerTwoName
            , Boolean playerOneWinner
            , Boolean playerTwoWinner) {
        this.userId = userId;
        this.playerOneName = playerOneName;
        this.playerTwoName = playerTwoName;
        this.playerOneWinner = playerOneWinner;
        this.playerTwoWinner = playerTwoWinner;
    }
}
