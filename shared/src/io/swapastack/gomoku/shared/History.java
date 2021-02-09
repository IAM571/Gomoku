package io.swapastack.gomoku.shared;

/**
 * This class is used as helper object to assemble the HistoryAll message.
 *
 * @author Dennis Jehle
 */
public class History {
    public String playerOneName;
    public String playerTwoName;
    public Boolean playerOneWinner;
    public Boolean playerTwoWinner;

    public History(String playerOneName, String playerTwoName, Boolean playerOneWinner, Boolean playerTwoWinner) {
        this.playerOneName = playerOneName;
        this.playerTwoName = playerTwoName;
        this.playerOneWinner = playerOneWinner;
        this.playerTwoWinner = playerTwoWinner;
    }
}
