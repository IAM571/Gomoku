package io.swapastack.gomoku.shared;

import java.util.ArrayList;

/**
 * This class represents the HistoryAll message specified in the network standard.
 * This message is used to send **all** game history entries saved on the server to the client.
 *
 * @author Dennis Jehle
 */
public class HistoryAll {
    public MessageType messageType = MessageType.HistoryAll;
    public ArrayList<History> history;

    public HistoryAll() {
        history = new ArrayList<History>();
    }

    /**
     * This method is used to populate the history ArrayList.
     *
     * @param playerOneName name of player one, not empty, not null
     * @param playerTwoName name of player two, not empty, not null
     * @param playerOneWinner true if player one is the winner
     * @param playerTwoWinner true if player two is the winner
     *
     * @author Dennis Jehle
     */
    public void appendEntry(String playerOneName, String playerTwoName, Boolean playerOneWinner, Boolean playerTwoWinner) {
        history.add(new History(playerOneName, playerTwoName, playerOneWinner, playerTwoWinner));
    }
}
