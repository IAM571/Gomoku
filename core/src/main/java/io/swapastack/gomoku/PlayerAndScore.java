package io.swapastack.gomoku;

public class PlayerAndScore{

    private String player_name;
    private int score;

    /**
     *
     * @param player_name
     * @param score
     */
    public PlayerAndScore(String player_name, int score) {
        this.player_name = player_name;
        this.score = score;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public void setPlayer_name(String player_name) {
        this.player_name = player_name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
