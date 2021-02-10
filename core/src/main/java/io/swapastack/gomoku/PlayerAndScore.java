package io.swapastack.gomoku;

public class PlayerAndScore{

    private String player_name;
    private int score;

    /**
     * @author Ibtsam Ali Mahmood
     * Spieler und Punkte Klasse
     * Hier wird der Name und der Punktestand der Spieler zurückgegeben. Das ist relevant für die Rangliste.
     * @see RankingScreen
     * @param player_name der Name des Spielers
     * @param score Die Punkte bei Sieg oder Niederlage
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
