package checkers;

import java.util.ArrayList;
import java.util.List;

public class Game {
    public enum Result {
        None, WHITE_WIN, BLACK_WIN, TIE
    }

    private Board board;
    private Player playerWhite;
    private Player playerBlack;
    private boolean isWhiteTurn = true;
    private int round = 1;
    private Result result = Result.None;

    public Game() {
        board = new Board();
        playerWhite = new Player(Color.White);
        playerBlack = new Player(Color.Black);
    }

    public Game(Player player1, Player player2) {
        this.board = new Board();
        this.isWhiteTurn = false;

    }

    public void move(int startIndex, int endIndex) {

    }

}
