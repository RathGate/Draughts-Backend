import checkers.Board;
import checkers.Color;
import checkers.Piece;

import java.awt.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        board.squares[22].piece = new Piece(Color.Black, Piece.PieceType.Man);
        board.squares[21].piece = new Piece(Color.Black, Piece.PieceType.Man);

        board.squares[26].piece = null;
        board.squares[5].piece = null;
        board.squares[25].piece = new Piece(Color.White, Piece.PieceType.King);
        board.print(false);
        System.out.println("Possible moves for white : " +board.getLegalMoves(Color.White));
        System.out.println("Possible moves for black : " +board.getLegalMoves(Color.Black));
    }
}