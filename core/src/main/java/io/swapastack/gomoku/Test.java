package io.swapastack.gomoku;

public class Test {
    private static final int screen_width = 1280;
    private static final int screen_height = 720;
    private static final int grid_size_ = 15;
    private static final float line_width = 5.f;
    private static final float grid_width = 515;
    private static final float grid_tiles = 14;
    private static final float dimension_one_tile = grid_width/grid_tiles;
    private static final float x_min = 382.5f;
    private static final float y_min = 102.5f;
    private static final float x_max = screen_width -x_min;
    private static final float y_max = screen_height -y_min;


    //varuiablen raussuchen die relevant sind wie z.B Screen Witdht etc.

    public static void main (String[] arg) {

        //Welche Kacheln kommt es rein !!!ACHTUNG KACHEL POS BEGINNT BEI 0 UND ENDET BEI 13!!!
        findTilesPosition(300,450); //keine
        findTilesPosition(160,220); //keine
        findTilesPosition(600,550); // X Kaxhel Pos: 5 Y Kachel Pos: 12
        findTilesPosition(700,400); // X Kaxhel Pos: 8 Y Kachel Pos: 8
        findTilesPosition(800,200); // X Kaxhel Pos: 11 Y Kachel Pos: 2
        //Welche Pixel sind welche Kacheln
        findPixels(5,12);
        findPixels(8,8);
        findPixels(11,2);


    }

    private static void findTilesPosition(int xPosMouseClick , int yPosMouseClick){
        System.out.println("X Pos: " + xPosMouseClick + " Y Pos: " + yPosMouseClick);

        if (xPosMouseClick >=x_min && yPosMouseClick >= y_min && xPosMouseClick <= x_max && yPosMouseClick <= y_max){

            float left_zero_point_x = xPosMouseClick-x_min;
            float left_zero_point_y = yPosMouseClick-y_min;

            float tile_position_x = left_zero_point_x/dimension_one_tile;
            float tile_position_y = left_zero_point_y/dimension_one_tile;
            int tile_pos_x = (int) tile_position_x;
            int tile_pos_y = (int) tile_position_y;

            System.out.println("X Kachel Pos: " + tile_pos_x + " Y Kachel Pos: " + tile_pos_y);

        }else{
            System.out.println("Nicht in Kacheln ");
        }
    }

    private static void findPixels(int tile_x,int tile_y){
        System.out.println("Kachel X: " + tile_x + "Kachel Y: " + tile_y);

        if (tile_x>= 0 && tile_x< 15 && tile_y >= 0 && tile_y <15){

            float pixel_x = tile_x*dimension_one_tile;
            float pixel_y = tile_y*dimension_one_tile;
            float X_Pixel_in_Game = pixel_x + x_min;
            float Y_Pixel_in_Game = pixel_y + y_min;

            System.out.println("X Pixel Position: " + X_Pixel_in_Game + " Y Pixel Position: " + Y_Pixel_in_Game);
        }else {
            System.out.println("In keiner Kachel geklickt.");
        }


    }
}
