package io.swapastack.gomoku;

import com.badlogic.gdx.Gdx;

public class GameScreenModel {
    // grid dimensions
    private static final int grid_size_ = 15;
    private static final float padding = 100.f;
    private static final float line_width = 5.f;
    //Count of tiles
    private static final int grid_tiles = grid_size_ - 1;
    //grid begin x
    private static final float grid_x_min = 380.f + (line_width / 2);
    //grid begin y
    private static final float grid_y_min = 100.f + (line_width / 2);
    //grid end x
    private static final float grid_x_max = 1280 - grid_x_min;
    //grid end y
    private static final float grid_y_max = 720 - grid_y_min;
    //Width of grid
    private static final float grid_width = 1280 - (2 * grid_x_min);
    //dimensions of one tile
    private static final float dimension_one_tile = grid_width / grid_tiles;

    public GameScreenModel (){
    }

    public Tuple findTilesPosition(int xPosMouseClick, int yPosMouseClick) {
        System.out.println("X Pos: " + xPosMouseClick + " Y Pos: " + yPosMouseClick);

        if (xPosMouseClick >= grid_x_min && yPosMouseClick >= grid_y_min && xPosMouseClick <= grid_x_max && yPosMouseClick <= grid_y_max) {

            float left_zero_point_x = xPosMouseClick - grid_x_min;
            float left_zero_point_y = yPosMouseClick - grid_y_min;

            float tile_position_x = left_zero_point_x / dimension_one_tile;
            float tile_position_y = left_zero_point_y / dimension_one_tile;
            int tile_pos_x = (int) tile_position_x;
            int tile_pos_y = (int) tile_position_y;

            System.out.println("X Kachel Pos: " + tile_pos_x + " Y Kachel Pos: " + tile_pos_y);
            Tuple tuple = new Tuple<Integer>(tile_pos_x, tile_pos_y);
            return tuple;

        } else {
            System.out.println("Nicht in Kacheln ");
            return null;
        }
    }

    public Tuple findPixels(int tile_x, int tile_y) {
        System.out.println("Kachel X: " + tile_x + "Kachel Y: " + tile_y);

        if (tile_x >= 0 && tile_x < grid_size_ && tile_y >= 0 && tile_y < grid_size_) {

            float pixel_x = 0.5f*dimension_one_tile + tile_x * dimension_one_tile;
            float pixel_y = 0.5f*dimension_one_tile + tile_y * dimension_one_tile;
            float X_Pixel_in_Game = pixel_x + grid_x_min;
            float Y_Pixel_in_Game = pixel_y + grid_y_min;

            System.out.println("X Pixel Position: " + X_Pixel_in_Game + " Y Pixel Position: " + Y_Pixel_in_Game);
            return new Tuple((int) X_Pixel_in_Game,(int) Y_Pixel_in_Game);
        } else {
            System.out.println("In keiner Kachel geklickt.");
            return null;
        }


    }
}
