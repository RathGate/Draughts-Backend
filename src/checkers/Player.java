package checkers;

import java.security.InvalidParameterException;

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
}
