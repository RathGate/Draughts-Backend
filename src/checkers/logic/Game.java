package checkers.logic;

import checkers.Player;
import checkers.board.Board;
import checkers.board.Color;

import java.awt.*;

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
        playerWhite = new Player(checkers.board.Color.White);
        playerBlack = new Player(Color.Black);
    }

    public Game(Player player1, Player player2) {
        this.board = new Board();
        this.isWhiteTurn = false;

    }


    public boolean movePiece(Point startPoint, Point endPoint) {
        if (startPoint == null || endPoint == null) {
            return false;
        }
        return movePiece(Board.toIndex(startPoint), Board.toIndex(endPoint));
    }
    public boolean movePiece(int startIndex, int endIndex) {
        return true;
    }

    public void endPlayerRound() {

    }
}
