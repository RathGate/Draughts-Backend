package checkers.logic;
import checkers.board.Board;
import checkers.board.Color;
import checkers.board.Piece;

import java.awt.*;

public class Rules {

    public static boolean isValidMove(Game game, int startIndex, int endIndex) {
        return isValidMove(game.getBoard(), game.isWhiteTurn, startIndex, endIndex, game.lastSkipInTurn);
    }
    public static boolean isValidMove(Board board, boolean isWhiteTurn, int startIndex, int endIndex, Move lastSkipInTurn) {

        int lastSkipIndex = lastSkipInTurn != null ? lastSkipInTurn.endIndex : -1;
        // Board check
        if (board == null) {
            System.out.println("Board is not valid");
            return false;
        // Indexes check
        } else if (!isValidIndex(startIndex) || !isValidIndex(endIndex)) {
            System.out.println("Invalid indexes");
            return false;
        // Additional indexes check if not first move of the player's round
        } else if (isValidIndex(lastSkipIndex) && lastSkipIndex != startIndex) {
            System.out.println("Invalid movement after skip");
            return false;
        }

        return isValidPieceMovement(board, isWhiteTurn, startIndex, endIndex) && isValidPieceDirection(board, isWhiteTurn, startIndex, endIndex);
    }

    public static boolean isValidPieceDirection(Board board, boolean isWhiteTurn, int startIndex, int endIndex) {
        if (board == null || !isValidIndex(startIndex) || !isValidIndex(endIndex)) {
            System.out.println("Board or indexes are not valid");
            return false;
        }

        // Checks if move is a diagonal
        Point startPoint = toPoint(startIndex);
        Point endPoint = toPoint(endIndex);
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
        if (board == null || !isValidIndex(startIndex) || !isValidIndex(endIndex)) {
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
        if (isValidIndex(midIndex)) {
            if (midPiece == null) {
                System.out.println("Middle position is empty");
                return false;
            } else if ((startPiece.color == Color.White) != isWhiteTurn) {
                System.out.println("Middle position occupied by ally piece");
            }
        }
        return true;
    }



    public static boolean isValidIndex(int index) { return (index >= 0 && index < 32); }
    public static boolean isValidPoint(int x, int y) { return isValidPoint(new Point(x, y)) ;}
    public static boolean isValidPoint(Point point) {
        if (point == null) {
            return false;
        }

        if (point.x < 0 || point.x > 7 ||
                point.y < 0 || point.y > 7) {
            return false;
        }

        return !(point.x % 2 == point.y % 2);
    }
    public static Point toPoint(int index) {
        if (!isValidIndex(index)) {
            return new Point(-1, -1);
        }
        int y = index / 4;
        int x = 2 * (index % 4) + (y + 1) % 2;
        return new Point(x, y);
    }
    public static int toIndex(Point point) {
        return toIndex(point.x, point.y);
    }
    public static int toIndex(int x, int y) {
        if (!isValidPoint(x, y)) {
            return -1;
        }
        return y * 4 + x / 2;
    }


    public static boolean isKingToBePromoted(int index, Piece piece) {
        if (piece == null || piece.pieceType == Piece.PieceType.King) {
            return false;
        }
        Point endPoint = toPoint(index);
        return piece.pieceType == Piece.PieceType.Man && ((endPoint.y == 0 && piece.color == Color.White) ||
                (endPoint.y == 7 && piece.color == Color.Black));
    }

    public static boolean IsValidBoardState(String state) {
        String re = "^(?:W(?<whites>(K?(?:3[0-2]|[12][0-9]|[0-9]))(?:,K?(?:3[0-2]|[12][0-9]|[0-9]))*)(?::?(?=B))*)?(?:B(?<blacks>(K?(?:3[0-2]|[12][0-9]|[0-9]))(?:,K?(?:3[0-2]|[12][0-9]|[0-9]))*))?$";
        return state.length() != 0 && state.matches(re);
    }
}
