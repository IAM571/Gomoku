package io.swapastack.gomoku;

import com.badlogic.gdx.graphics.Color;

import javax.naming.Name;

public enum Player {
    ONE(Color.WHITE, "Player 1"), TWO(Color.CORAL, "Player 2");

    Player(final Color colour, final String name) {
        this.colour = colour;
        this.name = name;
    }
    private Color colour;

    public Color getColour() {
        return colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}