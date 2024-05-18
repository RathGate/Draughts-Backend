import checkers.board.Board;
import checkers.board.Color;
import checkers.board.Piece;
import checkers.logic.Game;
import checkers.logic.Move;
import checkers.logic.Rules;

import static checkers.board.Board.toBoard;

public class Main {
    public static void main(String[] args) {
        String state = "W5,26,27,28,29,30,31,17,32:B2,3,16,6,7,8,24";
        Game game = new Game();
        game.setBoard(toBoard(state));

        game.print();
        game.makeMove(26, 19);
        game.print();
        game.makeMove(19,10);
        game.print();
        game.makeMove(10,3);
        game.print();
        game.makeMove(6,9);
        game.print();
        game.makeMove(27,23);
        game.print();
    }
}