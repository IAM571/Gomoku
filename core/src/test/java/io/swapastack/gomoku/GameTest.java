package io.swapastack.gomoku;

import com.badlogic.gdx.Game;
import org.junit.Assert;
import org.junit.Test;

public class GameTest {
    @Test
    public void findTilesPosition ( ) {
        GameScreenModel gameScreenModel = new GameScreenModel();
        Tuple ergebnis = gameScreenModel.findTilesPosition(300,450);
        Assert.assertNull(ergebnis);

        Tuple ergebnis1 = gameScreenModel.findTilesPosition(600,550);
        Assert.assertEquals(6,ergebnis1.first);
        Assert.assertEquals(12,ergebnis1.second);

        Tuple ergebnis2 = gameScreenModel.findTilesPosition(800,200);
        Assert.assertEquals(11,ergebnis2.first);
        Assert.assertEquals(3,ergebnis2.second);
    }

    @Test
    public void findPixelFromTiles (){
        GameScreenModel gameScreenModel = new GameScreenModel();
        Tuple ergebnis = gameScreenModel.findPixels(3,4);
        Assert.assertEquals(492,ergebnis.first);
        Assert.assertEquals(249,ergebnis.second);

        Tuple ergebnis1 = gameScreenModel.findPixels(0,13);
        Assert.assertEquals(382,ergebnis1.first);
        Assert.assertEquals(580,ergebnis1.second);

        Tuple ergebnis2 = gameScreenModel.findPixels(13,7);
        Assert.assertEquals(860,ergebnis2.first);
        Assert.assertEquals(360,ergebnis2.second);

    }
    @Test
    public void player_change (){
        GameScreenModel gameScreenModel = new GameScreenModel();
        Assert.assertEquals(Player.ONE,gameScreenModel.getCurrent_player());
        gameScreenModel.change_player();
        Assert.assertEquals(Player.TWO,gameScreenModel.getCurrent_player());

    }
    //Farbwechseltest für Eröffnungsregel 1
    @Test
    public void test_name_change(){
        GameScreenModel gameScreenModel = new GameScreenModel();
        String name1 = Player.ONE.getName();
        String name2 = Player.TWO.getName();
        gameScreenModel.change_player_colour();
        Assert.assertEquals(name2,Player.ONE.getName());
        Assert.assertEquals(name1, Player.TWO.getName());
    }
    //Test für Eröffnungsregel
    @Test
    public void test_openingrule(){
        GameScreenModel gameScreenModel = new GameScreenModel();
        gameScreenModel.setGamestone_position(1,2);
        int temp1 = gameScreenModel.handle_rules_after_gamestone();
        gameScreenModel.setGamestone_position(2,2);
        int temp2 = gameScreenModel.handle_rules_after_gamestone();
        Assert.assertEquals(4, temp1);
        Assert.assertEquals(4, temp2);
    }
    //Test openingrule 2
    @Test
    public void openingrule_2(){
        GameScreenModel gameScreenModel = new GameScreenModel();
        gameScreenModel.setGamestone_position(1,2);
        gameScreenModel.handle_rules_after_gamestone();
        gameScreenModel.setGamestone_position(2,2);
        gameScreenModel.handle_rules_after_gamestone(); //default case is openingrule 2
        Assert.assertEquals(Player.TWO,gameScreenModel.getCurrent_player());
    }
    @Test
    public void openingrule_3() {
        GameScreenModel gameScreenModel = new GameScreenModel();
        gameScreenModel.setGamestone_position(1, 2);
        gameScreenModel.handle_rules_after_gamestone();
        gameScreenModel.setGamestone_position(2, 2);
        gameScreenModel.handle_rules_after_gamestone();
        gameScreenModel.setGamestone_position(2, 5);
        gameScreenModel.setOpening_rule(3);
        Assert.assertEquals(Player.TWO, gameScreenModel.getCurrent_player());
        String name1 = Player.ONE.getName();
        String name2 = Player.TWO.getName();
        gameScreenModel.setGamestone_position(3,3);
        gameScreenModel.handle_rules_after_gamestone();
        Assert.assertEquals(name2, Player.ONE.getName());
        Assert.assertEquals(name1, Player.TWO.getName());
        Assert.assertEquals(Player.ONE, gameScreenModel.getCurrent_player());
        gameScreenModel.setGamestone_position(4,6);
        gameScreenModel.handle_rules_after_gamestone();
        Assert.assertEquals(Player.ONE.getName(),name1);
        Assert.assertEquals(Player.TWO.getName(), name2);
        Assert.assertEquals(Player.ONE, gameScreenModel.getCurrent_player());
    }

    @Test //horizontal win true
    public void win_condition_h() {
        GameScreenModel gameScreenModel = new GameScreenModel();
        gameScreenModel.setGamestone_position(4, 5);
        gameScreenModel.setGamestone_position(5, 5);
        gameScreenModel.setGamestone_position(6, 5);
        gameScreenModel.setGamestone_position(7, 5);
        gameScreenModel.setGamestone_position(8, 5);
        gameScreenModel.win_condition();
        Assert.assertEquals(true, gameScreenModel.win_condition());

    }
    @Test //vertical win true
    public void win_condition_v() {
        GameScreenModel gameScreenModel = new GameScreenModel();
        gameScreenModel.setGamestone_position(4, 4);
        gameScreenModel.setGamestone_position(4, 5);
        gameScreenModel.setGamestone_position(4, 6);
        gameScreenModel.setGamestone_position(4, 7);
        gameScreenModel.setGamestone_position(4, 8);
        gameScreenModel.win_condition();
        Assert.assertEquals(true, gameScreenModel.win_condition());

    }
    @Test //diagonal win true
    public void win_condition_d() {
        GameScreenModel gameScreenModel = new GameScreenModel();
        gameScreenModel.setGamestone_position(4, 4);
        gameScreenModel.setGamestone_position(5, 5);
        gameScreenModel.setGamestone_position(6, 6);
        gameScreenModel.setGamestone_position(7, 7);
        gameScreenModel.setGamestone_position(8, 8);
        gameScreenModel.win_condition();
        Assert.assertEquals(true, gameScreenModel.win_condition());
    }

    @Test //no win => false
    public void no_win_condition() {
        GameScreenModel gameScreenModel = new GameScreenModel();
        gameScreenModel.setGamestone_position(4, 5);
        gameScreenModel.setGamestone_position(5, 5);
        gameScreenModel.setGamestone_position(6, 5);
        gameScreenModel.setGamestone_position(7, 5);
        Assert.assertEquals(false, gameScreenModel.win_condition());

    }

}
