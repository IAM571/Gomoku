package io.swapastack.gomoku;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;

/**
 * @author Ibtsam Ali Mahmood
 * Deklarierung und Initialisierung einiger globaler Varibalen.
 */
public class GameScreenModel {
    // grid dimensions
    private static final int grid_size_ = 15;
    private static final float line_width = 5.f;
    //Count of tiles
    private static final int grid_tiles = grid_size_ - 1;
    //Width of grid
    private static final float grid_width = 515;
    //dimensions of one tile
    private static final float dimension_one_tile = grid_width / grid_tiles;
    //grid begin x
    private static final float grid_x_min = 380.f + (line_width / 2) - dimension_one_tile * 0.5f;
    //grid begin y
    private static final float grid_y_min = 100.f + (line_width / 2) - dimension_one_tile * 0.5f;
    //grid end x
    private static final float grid_x_max = 1280 - grid_x_min;
    //grid end y
    private static final float grid_y_max = 720 - grid_y_min;
    //current Player
    private Player current_player;
    //Array to save MouseClick
    private Player[][] gamestone_positions;
    // Turn counter
    private int counter;
    //Openingrule
    private int opening_rule;


    /**
     * GameScreenModel Konstruktor
     * Manche globale Varibalen werden hier Initialisiert.
     */
    public GameScreenModel() {
        current_player = Player.ONE;
        gamestone_positions = new Player[grid_size_][grid_size_];
        counter = 1;
        opening_rule = 0;
    }

    /**
     * Hier werden die Kachelpositionen eines Mausklicks berechnet.
     * Es wird ein "neues" Koordinatensystem aufgestellt dessen Nullpunkt an der linken unteren Ecke
     * des Spielfeldes beginnt. Ab da werden die Mausklicke umgerechnet in die 14 Kacheln
     * @param xPosMouseClick Die X Koordinate des Mausklicks
     * @param yPosMouseClick Die Y Koordinate des Mausklicks
     * Es wird erst geprüft ob der Mausklick innerhalb der Grenzen des "neuen" Koordinatensystems ist. Falls ja werden
     * vom Klick die Ränder, also die Flächen außerhalb des Gitters abgezogen. Dann werden diese durch die die Fläche einer
     * Kachel geteilt. Man erhält den einen Wert der abgerundet der X/Y Kachelposition entspricht.
     * @return Das Tuple der X und Y Position als Kachelposition wird zurückgegeben.
     */
    public Tuple findTilesPosition(int xPosMouseClick, int yPosMouseClick) {
        if (xPosMouseClick >= grid_x_min && yPosMouseClick >= grid_y_min && xPosMouseClick <= grid_x_max && yPosMouseClick <= grid_y_max) {

            float left_zero_point_x = xPosMouseClick - grid_x_min;
            float left_zero_point_y = yPosMouseClick - grid_y_min;

            float tile_position_x = left_zero_point_x / dimension_one_tile;
            float tile_position_y = left_zero_point_y / dimension_one_tile;
            int tile_pos_x = (int) tile_position_x;
            int tile_pos_y = (int) tile_position_y;

            Tuple tuple = new Tuple<Integer>(tile_pos_x, tile_pos_y);
            return tuple;

        } else {

            return null;
        }
    }

    /**
     * Hier werden die oben berechneten Kachelpositionen übergeben um diese in Pixel umzurechnen
     * damit die Steine auch bei leichtem daneben klicken an deren richtige position gesetzt werden.
     * Hier wird die findTilesPosition Funktion rükcwärts berechnet.
     * @param tile_x Die oben berechnete Kachelposition auf der X Achse wird übergeben
     * @param tile_y Die oben berechnete Kachelposition auf der Y Achse wird übergeben
     * @return Das Tuple der berechneten Pixel wird zurückgegeben
     */
    public Tuple findPixels(int tile_x, int tile_y) {


        if (tile_x >= 0 && tile_x <= grid_size_ && tile_y >= 0 && tile_y <= grid_size_) {

            float pixel_x = 0.5f * dimension_one_tile + tile_x * dimension_one_tile;
            float pixel_y = 0.5f * dimension_one_tile + tile_y * dimension_one_tile;
            float X_Pixel_in_Game = pixel_x + grid_x_min;
            float Y_Pixel_in_Game = pixel_y + grid_y_min;


            return new Tuple((int) X_Pixel_in_Game, (int) Y_Pixel_in_Game);
        } else {

            return null;
        }


    }

    /**
     * Überprüfung der freien Position und zuordnung des aktuellen Spieler
     * @param tile_pos_x X Kachel Position
     * @param tile_pos_y Y Kachel Position
     * @return falls die Position besetzt ist wird false ausgegeben, wenn nicht, dann true.
     * Der Conuter (Zähler) wird immer um eins erhöht, sobald der aktuellle spieler einen Stein gesetzt hat.
     * */
    public boolean setGamestone_position(int tile_pos_x, int tile_pos_y) {

        if (gamestone_positions[tile_pos_x][tile_pos_y] != null) {
            return false;
        } else {
            gamestone_positions[tile_pos_x][tile_pos_y] = current_player;
            counter++;
            return true;
        }
    }


    /**
     * Funktion, die den Spieler wechselt.
     */
    public void change_player() {
        if (this.getCurrent_player() == Player.ONE) {
            this.setCurrent_player(Player.TWO);
        } else {
            this.setCurrent_player(Player.ONE);
        }

    }

    /**
     * Hier wird die SWAP2 Regel überpüft bis der Zähler vier (da er bei 1 beginnt) ist, ist der Spieler 1 an der Reihe.
     * Ab vier wird der zweite Spieler nach seiner Auswahl gefragt.
     * Je nach dem für was man sich entscheidet und welche Zahl der Zähler hat werden die verschiedenen Optionen ausgeführt.
     * Bzw es werden im GameScreen durch die Rückgabewerte die verschiedenen Dialogfelder aufgerufen.
     * @return 1 wird zurückgegeben nach dem ersten Zug um den SWAP2-Regel Dialog aurufen zu lassen.
     * @return 2 wird zurückgegeben, wenn der Spieler sich für Option 3 entschieden hat und der andere Spieler den
     * Farbtauschdialog braucht
     * @return 4 wenn der Standardfall eintritt, also nichts passiert. Siehe GameScreen SwitchCase
     * @see GameScreen
     */
    public int handle_rules_after_gamestone() {

        if (this.getCounter() == 2) {
            change_player();
        }
        if (this.getCounter() == 3) {
            change_player_colour();
        }
        if (this.getCounter() == 4 || opening_rule == 3) {
            if (this.getCounter() == 4) {
                change_player();
                change_player_colour();
                return 1;
            } else if (this.getCounter() == 5) {
                this.change_player();
                change_player_colour();

            } else if (this.getCounter() == 6) {
                opening_rule = 0;
                change_player_colour();
                return 2;
            }
        } else {
            change_player();

        }
        return 4;
    }

    /**
     * Ein einfacher tausch der Farben, mit einer temporären Variable.
     */
    public void change_player_colour() {
        String name_temp = Player.ONE.getName();
        Player.ONE.setName(Player.TWO.getName());
        Player.TWO.setName(name_temp);
    }

    /**
     * @return gibt die Zahl des Counters zurück für die SWAP2 Regel
     */
    public int getCounter() {
        return counter;
    }

    /**
     * @return Rückgabe der Spielstein Positionen als Array
     */
    public Player[][] getGamestone_positions() {
        return gamestone_positions;
    }


    /**
     * Hier wird die Gewinnbedingung geprüft. Horizontal, Vertikal und Diagonal.
     * @return Sobald fünf Steine Horizontal, Vertikal oder Diagonal in einer Reihe sind wird True zurückgegeben und der
     * aktuelle Spieler hat gewonnen. Bei der Diagonalen sind vier verschiedene Funktionen. Zwei, welche die
     * Bedingung von links nach rechts nach oben überprüfen und zwei die nach unten andersherum prüfen.
     * Die überprüfung findet statt, in dem über das Spielfeld iteriert wird und die Kachel n mit der Kachel n+1 verglichen wird.
     */
    //Winning condition
    //vertical
    public boolean win_condition() {
        int gamestone_counter = 0;
        for (int column = 0; column < grid_size_; column++) {
            for (int row = 0; row < grid_size_; row++) {
                if (row < grid_size_ - 1) {
                    if (gamestone_positions[column][row] == gamestone_positions[column][row + 1] &&
                            gamestone_positions[column][row] != null) {
                        gamestone_counter++;
                    } else {
                        gamestone_counter = 0;
                    }
                    if (gamestone_counter == 4) {
                        return true;
                    }
                }
            }
        }
        //horizontal
        gamestone_counter = 0;
        for (int row = 0; row < grid_size_; row++) {
            for (int column = 0; column < grid_size_; column++) {
                if (column < grid_size_ - 1) {
                    if (gamestone_positions[column][row] == gamestone_positions[column + 1][row] &&
                            gamestone_positions[column][row] != null) {
                        gamestone_counter++;
                    } else {
                        gamestone_counter = 0;
                    }
                    if (gamestone_counter == 4) {
                        return true;
                    }
                }
            }
        }
        //Diagonal
        gamestone_counter = 0;
        int row = 0;
        int column = 0;
        for (int i = 0; i < grid_size_; i++) {
            row = i;
            column = 0;
            while (row < grid_size_ - 1 && column < grid_size_ - 1) {

                if (gamestone_positions[column][row] == gamestone_positions[column + 1][row + 1] &&
                        gamestone_positions[column][row] != null) {
                    gamestone_counter++;
                } else {
                    gamestone_counter = 0;
                }
                if (gamestone_counter == 4) {
                    return true;
                }
                row++;
                column++;

            }
        }
        for (int i = 0; i < grid_size_; i++) {
            column = i;
            row = 0;
            while (row < grid_size_ - 1 && column < grid_size_ - 1) {

                if (gamestone_positions[column][row] == gamestone_positions[column + 1][row + 1] &&
                        gamestone_positions[column][row] != null) {
                    gamestone_counter++;
                } else {
                    gamestone_counter = 0;
                }
                if (gamestone_counter == 4) {
                    return true;
                }
                row++;
                column++;

            }
        }

        for (int i = grid_size_ - 1; i > 0; i--) {
            row = i;
            column = 0;
            while (row > 0 && column < grid_size_ - 1) {

                if (gamestone_positions[column][row] == gamestone_positions[column + 1][row - 1] &&
                        gamestone_positions[column][row] != null) {
                    gamestone_counter++;
                } else {
                    gamestone_counter = 0;
                }
                if (gamestone_counter == 4) {
                    return true;
                }
                row--;
                column++;

            }
        }
        for (int i = grid_size_ - 1; i > 0; i--) {
            row = 1;
            column = i;
            while (row > 0 && column < grid_size_ - 1) {

                if (gamestone_positions[column][row] == gamestone_positions[column + 1][row - 1] &&
                        gamestone_positions[column][row] != null) {
                    gamestone_counter++;
                } else {
                    gamestone_counter = 0;
                }
                if (gamestone_counter == 4) {
                    return true;
                }
                row--;
                column++;

            }
        }

        return false;
    }

    // Es werden Variablen zurückgegeben, die man aufrufen muss.
    public Player getCurrent_player() {
        return current_player;
    }

    public void setCurrent_player(Player current_player) {
        this.current_player = current_player;
    }

    public int getOpening_rule() {
        return opening_rule;
    }

    public void setOpening_rule(int opening_rule) {
        this.opening_rule = opening_rule;
    }
}
