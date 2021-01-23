package io.swapastack.gomoku;

import org.junit.Assert;
import org.junit.Test;

public class GameTest {
    @Test
    public void findTilesPosition ( ) {
        GameScreenModel gameScreenModel = new GameScreenModel();
        Tuple ergebnis = gameScreenModel.findTilesPosition(300,450);
        Assert.assertNull(ergebnis);

        Tuple ergebnis1 = gameScreenModel.findTilesPosition(600,550);
        Assert.assertEquals(5,ergebnis1.first);
        Assert.assertEquals(12,ergebnis1.second);

        Tuple ergebnis2 = gameScreenModel.findTilesPosition(800,200);
        Assert.assertEquals(11,ergebnis2.first);
        Assert.assertEquals(2,ergebnis2.second);
    }

    @Test
    public void findPixelFromTiles (){
        GameScreenModel gameScreenModel = new GameScreenModel();
        Tuple ergebnis = gameScreenModel.findPixels(3,4);

        Assert.assertEquals(511,ergebnis.first);
        Assert.assertEquals(268,ergebnis.second);

        Tuple ergebnis1 = gameScreenModel.findPixels(0,13);
        Assert.assertEquals(400,ergebnis1.first);
        Assert.assertEquals(599,ergebnis1.second);

        Tuple ergebnis2 = gameScreenModel.findPixels(13,7);
        Assert.assertEquals(879,ergebnis2.first);
        Assert.assertEquals(378,ergebnis2.second);

    }
}
//Welche Kacheln kommt es rein !!!ACHTUNG KACHEL POS BEGINNT BEI 0 UND ENDET BEI 13!!!
       /* findTilesPosition(300,450); //keine
        findTilesPosition(160,220); //keine
        findTilesPosition(600,550); // X Kaxhel Pos: 5 Y Kachel Pos: 12
        findTilesPosition(700,400); // X Kaxhel Pos: 8 Y Kachel Pos: 8
        findTilesPosition(800,200); // X Kaxhel Pos: 11 Y Kachel Pos: 2
        //Welche Pixel sind welche Kacheln
        findPixels(5,12);
        findPixels(8,8);
        findPixels(11,2);
        Tuple abc = findTilesPosition(600,500);
        System.out.println(abc.first + " " + abc.second );
        Tuple y = findPixels(5,5);
        System.out.println(y.first + " " + y.second);*/
