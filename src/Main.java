import checkers.board.Board;
import checkers.board.Color;
import checkers.board.Piece;
import checkers.logic.Game;
import checkers.logic.Move;
import checkers.logic.Rules;

import java.util.ArrayList;
import java.util.List;

import static checkers.board.Board.toBoard;

public class Main {
    public static void main(String[] args) {
        String state = "W9,10,11,17,18,19,25,26,27:B15";
//        String state = "W10,11,17,9,27:BK15";

        Game game = new Game();

        game.setBoard(toBoard(state));

        game.print();
        List<Move> moves = game.getBoard().getCompleteSkips(14, 28);
        System.out.println(moves.size());
        for (Move move : moves) {
            System.out.println(move);
        }
    }
}