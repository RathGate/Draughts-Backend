package checkers.logic;
import checkers.board.Board;
import checkers.board.Color;
import checkers.board.Piece;

import java.awt.*;

public class Rules {


    public static boolean isValidMove(Game game, int startIndex, int endIndex) {
        return isValidMove(game.getBoard(), game.isWhiteTurn, startIndex, endIndex, game.lastSkipIndex);
    }
    public static boolean isValidMove(Board board, boolean isWhiteTurn, int startIndex, int endIndex, int skipIndex) {
        // Board check
        if (board == null) {
            System.out.println("Board is not valid");
            return false;
        // Indexes check
        } else if (!Board.isValidIndex(startIndex) || !Board.isValidIndex(endIndex)) {
            System.out.println("Invalid indexes");
            return false;
        // Additional indexes check if not first move of the player's round
        } else if (Board.isValidIndex(skipIndex) && skipIndex != startIndex) {
            System.out.println("Invalid movement after skip");
            return false;
        }

        return isValidPieceMovement(board, isWhiteTurn, startIndex, endIndex) && isValidPieceDirection(board, isWhiteTurn, startIndex, endIndex);
    }

    public static boolean isValidPieceDirection(Board board, boolean isWhiteTurn, int startIndex, int endIndex) {
        if (board == null || !Board.isValidIndex(startIndex) || !Board.isValidIndex(endIndex)) {
            System.out.println("Board or indexes are not valid");
            return false;
        }

        // Checks if move is a diagonal
        Point startPoint = Board.toPoint(startIndex);
        Point endPoint = Board.toPoint(endIndex);
        int dx = endPoint.x - startPoint.x;
        int dy = endPoint.y - startPoint.y;
        if (Math.abs(dx) != Math.abs(dy) || Math.abs(dx) > 2 || dx == 0) {
            return false;
        }

        // Checks if movement direction is legal for piece type
        Piece startPiece = board.getPieceAt(startIndex);
        if (startPiece == null) { return false; }
        if (startPiece.pieceType == Piece.PieceType.Man) {
            if (startPiece.color == Color.White && dy > 0 || startPiece.color == Color.Black && dy < 0) {
                System.out.println("Piece is not allowed to move in this direction");
                return false;
            }
        }

        // If move is not a skip, checks if skips are available for the player
        int midIndex = Board.getMiddleIndex(startIndex, endIndex);
        if (midIndex == -1) {
            Color currentColor = isWhiteTurn ? Color.White : Color.Black;
            if (board.getPossibleSkips(currentColor).size() != 0) {
                System.out.println("Skips are available for current player");
                return false;
            }
        }
        return true;
    }
    public static boolean isValidPieceMovement(Board board, boolean isWhiteTurn, int startIndex, int endIndex) {
        if (board == null || !Board.isValidIndex(startIndex) || !Board.isValidIndex(endIndex)) {
            System.out.println("Board or indexes are not valid");
            return false;
        }
        // Check if ending square is empty
        if (board.getPieceAt(endIndex) != null) {
            System.out.println("Ending position is already occupied");
            return false;
        }

        Piece startPiece = board.getPieceAt(startIndex);
        // Checks if starting piece exists and is of same color as current player
        if (startPiece == null || (startPiece.color == Color.White) != isWhiteTurn) {
            System.out.println("Starting position is wrong color or empty");
            return false;
        }

        // Checks if middle piece exists (= skip) and is of same color as current player
        int midIndex = Board.getMiddleIndex(startIndex, endIndex);
        Piece midPiece = board.getPieceAt(midIndex);
        if (Board.isValidIndex(midIndex)) {
            if (midPiece == null) {
                System.out.println("Middle position is empty");
                return false;
            } else if ((startPiece.color == Color.White) != isWhiteTurn) {
                System.out.println("Middle position occupied by ally piece");
            }
        }
        return true;
    }
}
