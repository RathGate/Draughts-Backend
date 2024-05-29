package checkers;

import checkers.board.Color;

public class Player {
    private Color color;
    private String username;

    public Player(Color color) {
        this.color = color;
    }

    public Player(String username, Color color) {
        this.username = username;
        this.color = color;
    }

    public Color getColor() { return this.color; }

    public void setColor(Color newColor) {
        this.color = newColor;
    }

}
