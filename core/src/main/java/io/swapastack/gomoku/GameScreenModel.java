package io.swapastack.gomoku;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;

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


    public GameScreenModel() {
        current_player = Player.ONE;
        gamestone_positions = new Player[grid_size_][grid_size_];
        counter = 1;
        opening_rule = 0;
    }

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

    public boolean setGamestone_position(int tile_pos_x, int tile_pos_y) {

        if (gamestone_positions[tile_pos_x][tile_pos_y] != null) {
            return false;
        } else {
            gamestone_positions[tile_pos_x][tile_pos_y] = current_player;
            counter++;
            return true;
        }
    }


    public void change_player() {
        if (this.getCurrent_player() == Player.ONE) {
            this.setCurrent_player(Player.TWO);
        } else {
            this.setCurrent_player(Player.ONE);
        }

    }

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

    public void change_player_colour() {
        String name_temp = Player.ONE.getName();
        Player.ONE.setName(Player.TWO.getName());
        Player.TWO.setName(name_temp);
    }

    public int getCounter() {
        return counter;
    }

    public Player[][] getGamestone_positions() {
        return gamestone_positions;
    }


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
                        System.out.println(getCurrent_player() + "Hat gewonnen");
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
                        System.out.println(getCurrent_player() + "Hat gewonnen");
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
                    System.out.println(getCurrent_player() + "Hat gewonnen");
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
                    System.out.println(getCurrent_player() + "Hat gewonnen");
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
                    System.out.println(getCurrent_player() + "Hat gewonnen");
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
                    System.out.println(getCurrent_player() + "Hat gewonnen");
                    return true;
                }
                row--;
                column++;

            }
        }

        return false;
    }


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
