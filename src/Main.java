import checkers.board.Board;
import checkers.board.Color;
import checkers.board.Piece;

import static checkers.board.Board.IsValidBoardState;
import static checkers.board.Board.toBoard;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        board.squares[22].piece = new Piece(Color.Black, Piece.PieceType.Man);
        board.squares[21].piece = new Piece(Color.Black, Piece.PieceType.Man);

        board.squares[26].piece = null;
        board.squares[5].piece = null;
        board.squares[25].piece = new Piece(Color.White, Piece.PieceType.King);
        board.print(false);
        String state = (board.toState());
        System.out.println(state);
        board = toBoard(state);
        assert board != null;
        board.print(false);
    }
}