package checkers.board;

import checkers.logic.Move;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static checkers.logic.Rules.*;

public class Board {
    public Square[] squares = new Square[32];

    // ----- BOARD : CONSTRUCTEURS ----- //
    public Board() {
        emptyBoard();
        // placing pieces on the board
        for (int i = 0; i < 8; i++) {
            squares[i].piece = new Piece(Color.Black);
            squares[31-i].piece = new Piece(Color.White);
        }
    }
    // ------------------------------- //

    // ----- BOARD : MODIFICATION ----- //
    public void emptyBoard() {
        for (int i = 0; i < squares.length; i++) {
            squares[i] = new Square();
        }
    }
    // -------------------------------- //

    // ----- TEMP : DEBUG FUNCTIONS ----- //
    public void print(boolean numbers) {
        boolean isInverted = true;
        String curr = "";
        for (int i = 0; i < 32; i++) {
            if (numbers) {
                curr = (i+1 > 9) ? ""+(i+1) : " " + (i+1);
            } else {
                curr = squares[i].toString();
            }
            if (i % 4 == 0) {
                System.out.print("\n+----+----+----+----+----+----+----+----+\n|");
                isInverted = !isInverted;
            }
            if (!isInverted) {
                System.out.print("    | "+curr+" |");
            } else {
                System.out.print(" "+curr+" |    |");
            }
        }
        System.out.print("\n+----+----+----+----+----+----+----+----+\n");
    }
    // ---------------------------------- //

    // ----- SQUARES : RETRIEVE PIECES ----- //
    // Retrieves the piece at a given index
    public Piece getPieceAt(int index) {
        if (!isValidIndex(index)) {
            return null;
        }
        return squares[index].piece;
    }
    // Retrieves a piece at a specific point
    public Piece getPieceAt(Point point) {
        int index = toIndex(point);
        return getPieceAt(index);
    }
    // ------------------------------------- //

    // ----- SQUARES : RETRIEVE SQUARE BETWEEN TWO SQUARES ----- //
    //Retrieves the square index between two squares
    public static int getMiddleIndex(int i1, int i2) {
        return toIndex(getMiddlePoint(toPoint(i1), toPoint(i2)));
    }
    public static Point getMiddlePoint(Point p1, Point p2) {
        // invalid point
        if (p1 == null || p2 == null) {
            return new Point(-1, -1);
        }
        // returns point
        return getMiddlePoint(p1.x, p1.y, p2.x, p2.y);
    }

    //Retrieves the square index between two squares
    public static Point getMiddlePoint(int x1, int y1, int x2, int y2) {
        // Invalid point
        if (!isValidPoint(x1, y1) || !(isValidPoint(x2, y2))) {
            return new Point(-1, -1);
        }
        // Gets middle position
        int dx = x2 - x1, dy = y2 - y1;
        if (Math.abs(dx) != Math.abs(dy) || Math.abs(dx) != 2) {
            return new Point(-1, -1);
        }
        // Returns point
        return new Point(x1 + dx / 2, y1+dy / 2);
    }
    // ------------------------------------------------ //

    // SQUARES : RETRIEVES ALL FOUR POINTS AROUND SQUARE ----- //
    //Retrieves all for positions between a square, at a given distance
    public static List<Point> getPointsAround(Point origin, Piece piece, int distance) {
        List<Point> points = new ArrayList<Point>();

        if (distance < 1 || distance > 2) {
            return points;
        }
        // Can only move up if white or king
        if (piece.isKing() || !piece.isWhite()) {
            points.add(new Point(origin.x + distance, origin.y + distance));
            points.add(new Point(origin.x - distance, origin.y + distance));
        }
        // Can only move down if black or king
        if (piece.isKing() || piece.isWhite()) {
            points.add(new Point(origin.x + distance, origin.y - distance));
            points.add(new Point(origin.x - distance, origin.y - distance));
        }

        // Checks points validity
        List<Point> finalPoints = new ArrayList<>();
        for (Point point : points) {
            if (isValidPoint(point)) {
                finalPoints.add(point);
            }
        }
        // Returns points
        return finalPoints;
    }

    //Retrieves all for positions between a square, at a given distance
    public List<Point> getPointsAround(int index, int distance) {
        List<Point> points = new ArrayList<>();
        if (!isValidIndex(index)) {
            return points;
        }
        Piece piece = squares[index].piece;
        if (piece == null) {
            return points;
        }
        return Board.getPointsAround(toPoint(index), piece, distance);
    }
    // ------------------------------------------------------ //

    // ----- PIECES : RETRIEVES ALL PIECES POSITIONS ----- //
    public List<Point> findPieces() {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            Piece piece = getPieceAt(i);
            if (piece != null) {
                points.add(toPoint(i));
            }
        }
        return points;
    }

    public List<Point> findPieces(Color color) {
        List<Point> points = new ArrayList<>();

        for (int i = 0; i < 32; i++) {
            Piece piece = getPieceAt(i);
            if (piece != null && piece.color == color) {
                points.add(toPoint(i));
            }
        }
        return points;
    }
    public List<Point> findPieces(Piece.PieceType pieceType) {
        List<Point> points = new ArrayList<>();

        for (int i = 0; i < 32; i++) {
            Piece piece = getPieceAt(i);
            if (piece != null && piece.pieceType == pieceType) {
                points.add(toPoint(i));
            }
        }
        return points;
    }
    public List<Point> findPieces(Color color, Piece.PieceType pieceType) {
        List<Point> points = new ArrayList<>();

        for (int i = 0; i < 32; i++) {
            Piece piece = getPieceAt(i);
            if (piece != null && piece.color == color && piece.pieceType == pieceType) {
                points.add(toPoint(i));
            }
        }
        return points;
    }
    // -------------------------------------------------- //

    // ----- PIECES : RETRIEVE MOVES AND SKIPS ----- //
    public List<Point> getPossibleMoves(Point origin) {
        return getPossibleMoves(toIndex(origin));
    }
    public List<Point> getPossibleMoves(int index) {
        List<Point> points = new ArrayList<>();
        if (!isValidIndex(index)) {
            return points;
        }
        Piece piece = squares[index].piece;
        if (piece == null) {
            return points;
        }
        Point origin = toPoint(index);

        points = getPointsAround(origin, piece, 1);

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            if (getPieceAt(point) != null) {
                points.remove(i--);
            }
        }
        return points;
    }
    public List<Point> getPossibleSkips(Point origin) {
        return getPossibleSkips(toIndex(origin));
    }
    public List<Point> getPossibleSkips(int index) {
        List<Point> points = new ArrayList<>();
        if (!isValidIndex(index)) {
            return points;
        }
        Piece piece = squares[index].piece;
        if (piece == null) {
            return points;
        }
        Point origin = toPoint(index);

        points = getPointsAround(origin, piece, 2);

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            if (!isValidSkip(index, toIndex(point))) {
                points.remove(i--);
            }
        }
        return points;
    }

    public List<Point> getPossibleSkips(Color color) {
        List<Point> checkers = findPieces(color);
        List<Point> skips = new ArrayList<>();

        for (Point checker : checkers) {
            skips.addAll(getPossibleSkips(checker));
        }
        return skips;
    }
    public boolean isValidSkip(int startIndex, int endIndex) {
        if (getPieceAt(endIndex) != null) {
            return false;
        }
        Piece startPiece = getPieceAt(startIndex);
        int middleIndex = getMiddleIndex(startIndex, endIndex);
        Piece middlePiece = getPieceAt(middleIndex);

        if (middlePiece == null) {
            return false;
        }
        return startPiece.color != middlePiece.color;
        // ------------------------------------------------------ //

    }

    // ----- PLAYER : RETRIEVE ALL POSSIBLE LEGAL MOVES ----- //
    public List<Move> getLegalMoves(Color color) {
        List<Move> legalMoves = new ArrayList<>();
        List<Point> checkers = findPieces(color);

        if (checkers.size() == 0) {
            return legalMoves;
        }

        for (Point checker : checkers) {
            List<Point> skips = getPossibleSkips(checker);
            int index = toIndex(checker);
            for (Point skip : skips) {
                Move m = new Move(index, toIndex(skip), true);
                legalMoves.add(m);
            }
        }

        if (legalMoves.size() == 0) {
            for (Point checker : checkers) {
                List<Point> moves = getPossibleMoves(checker);
                int index = toIndex(checker);
                for (Point move : moves) {
                    legalMoves.add(new Move(index, toIndex(move)));
                }
            }
        }
        return legalMoves;
    }
    public List<String> getLegalMovesStr(Color color) {
        List<Move> legalMoves = getLegalMoves(color);
        List<String> result = new ArrayList<>();
        for (Move move : legalMoves) {
            if (move == null) {
                result.add("");
                continue;
            }
            result.add(move.toString());
        }
        return result;
    }

    public String toState() {
        StringBuilder state = new StringBuilder();
        List<String> whites = new ArrayList<>();
        List<String> blacks = new ArrayList<>();


        for (int i = 0; i < 32; i++) {
            Piece piece = getPieceAt(i);
            if (piece == null) { continue; }

            String temp = piece.pieceType == Piece.PieceType.King ? "K" : "";
            if (piece.color == Color.White) {
                whites.add(temp + (i+1));
                continue;
            }
            blacks.add(temp+(i+1));
        }

        state.append("W").append(String.join(",",whites));
        state.append(":");
        state.append("B").append(String.join(",",blacks));
        return state.toString();
    }

    public static Board toBoard(String state) {
        if (!IsValidBoardState(state)) {
            return null;
        }
        Board board = new Board();
        board.emptyBoard();

        String re = "^(?:W(?<White>(K?(?:3[0-2]|[12][0-9]|[0-9]))(?:,K?(?:3[0-2]|[12][0-9]|[0-9]))*)(?::?(?=B))*)?(?:B(?<Black>(K?(?:3[0-2]|[12][0-9]|[0-9]))(?:,K?(?:3[0-2]|[12][0-9]|[0-9]))*))?$";
        Pattern pattern = Pattern.compile(re);
        Matcher matcher = pattern.matcher(state);
        boolean success = matcher.find();

        for (Color color : Color.values()) {
            try {
                String[] moves = success ? matcher.group(color.toString()).split(",") : null;
                if (moves == null || moves.length == 0) {
                    continue;
                }

                for (String move : moves) {
                    int index = Integer.parseInt(move.replaceAll("[\\D]", "")) - 1;
                    Piece.PieceType pieceType = move.charAt(0) == 'K' ? Piece.PieceType.King : Piece.PieceType.Man;
                    board.squares[index].piece = new Piece(color, pieceType);
                }
            } catch (java.lang.IllegalArgumentException ignored) {}
        }
        return board;
    }
    public boolean setPieceAt(int index, Piece piece) {
        if (!isValidIndex(index)) {
            return false;
        }
        squares[index].piece = piece;
        return true;
    }
    public boolean setPieceAt(int index, Color color, Piece.PieceType pieceType) {
        return setPieceAt(index, new Piece(color, pieceType));
    }

    public Board copy() {
        Board copy = new Board();
        copy.emptyBoard();

        for (int i = 0; i < 32; i++) {
            copy.squares[i] = new Square(squares[i]);
        }
        return copy;
    }

    public void movePiece(int startIndex, int endIndex) {
        Piece movingPiece = getPieceAt(startIndex);
        int midIndex = Board.getMiddleIndex(startIndex, endIndex);
        setPieceAt(endIndex, movingPiece);
        setPieceAt(midIndex, null);
        setPieceAt(startIndex, null);
        boolean isSkip = midIndex > 0;

        // Crown king
        if (isKingToBePromoted(endIndex, movingPiece)) {
            setPieceAt(endIndex, movingPiece.color, Piece.PieceType.King);
        }
    }

    public List<Move> getCompleteSkips(int startIndex, int endIndex) {
        if (!isValidIndex(endIndex)) {
            return getCompleteSkips(startIndex);
        }
        return getCompleteSkips(startIndex).stream()
                .filter(m -> m.endIndex == endIndex)
                .collect(Collectors.toList());
    }

    public List<Move> getCompleteSkips(int startIndex) {

        Board saved_board = this.copy();
        List<Move> result_skips = new ArrayList<>();

        List<Point> skips_from_index = getPossibleSkips(startIndex);
//        if (debug) { printTemp(startIndex, skips_from_index, level); }

        for (Point skip : skips_from_index) {
            Board temp_board = saved_board.copy();
            int end_index = toIndex(skip);
            temp_board.movePiece(startIndex, end_index);
            Move temp = new Move(startIndex, end_index,true, null, true);
            List <Move> movesFromIndex = temp_board.getCompleteSkips(end_index);
            if (movesFromIndex.isEmpty()) {
                    result_skips.add(temp);
            } else {
                for (Move move : movesFromIndex) {
                    move.addStepBefore(startIndex);
                    result_skips.add(move);
                }
            }
        }
        return result_skips;
    }
}

