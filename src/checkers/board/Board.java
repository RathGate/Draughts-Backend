package checkers.board;

import checkers.logic.Move;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Board {
    public Square[] squares = new Square[32];

    public Board() {
        emptyBoard();

        for (int i = 0; i < 8; i++) {
            squares[i].piece = new Piece(Color.Black);
            squares[31-i].piece = new Piece(Color.White);
        }
    }

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
    public Piece getPieceAt(int index) {
        if (!isValidIndex(index)) {
            return null;
        }
        return squares[index].piece;
    }
    public Piece getPieceAt(Point point) {
        int index = toIndex(point);
        return getPieceAt(index);
    }
    // ------------------------------------- //

    // ----- SQUARES : INDEX AND POINT CHECKING/CONVERSION ----- //
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
    // --------------------------------------------------------- //

    // ----- SQUARES : RETRIEVE SQUARE BETWEEN TWO SQUARES ----- //
    public static int getMiddleIndex(int i1, int i2) {
        return toIndex(getMiddlePoint(toPoint(i1), toPoint(i2)));
    }
    public static Point getMiddlePoint(Point p1, Point p2) {
        if (p1 == null || p2 == null) {
            return new Point(-1, -1);
        }
        return getMiddlePoint(p1.x, p1.y, p2.x, p2.y);
    }
    public static Point getMiddlePoint(int x1, int y1, int x2, int y2) {
        if (!isValidPoint(x1, y1) || !(isValidPoint(x2, y2))) {
            return new Point(-1, -1);
        }

        int dx = x2 - x1, dy = y2 - y1;
        if (Math.abs(dx) != Math.abs(dy) || Math.abs(dx) != 2) {
            return new Point(-1, -1);
        }

        return new Point(x1 + dx / 2, y1+dy / 2);
    }
    // ------------------------------------------------ //

    // SQUARES : RETRIEVES ALL FOUR POINTS AROUND SQUARE ----- //
    public static List<Point> getPointsAround(Point origin, Piece piece, int distance) {
        List<Point> points = new ArrayList<Point>();

        if (distance < 1 || distance > 2) {
            return points;
        }
        if (piece.isKing() || !piece.isWhite()) {
            points.add(new Point(origin.x + distance, origin.y + distance));
            points.add(new Point(origin.x - distance, origin.y + distance));
        }
        if (piece.isKing() || piece.isWhite()) {
            points.add(new Point(origin.x + distance, origin.y - distance));
            points.add(new Point(origin.x - distance, origin.y - distance));
        }

        List<Point> finalPoints = new ArrayList<>();
        for (Point point : points) {
            if (isValidPoint(point)) {
                finalPoints.add(point);
            }
        }
        return finalPoints;
    }
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
    public List<Integer> findPiecesIndex(Color color) {
        List<Integer> indexes = new ArrayList<>();

        for (int i = 0; i < 32 ; i++) {
            Piece piece = getPieceAt(i);
            if (piece != null && piece.color == color) {
                indexes.add(i);
            }
        }
        return indexes;
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
        return getPossibleMoves(Board.toIndex(origin));
    }
    public List<Point> getPossibleMoves(int index) {
        List<Point> points = new ArrayList<>();
        if (!Board.isValidIndex(index)) {
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
        return getPossibleSkips(Board.toIndex(origin));
    }
    public List<Point> getPossibleSkips(int index) {
        List<Point> points = new ArrayList<>();
        if (!Board.isValidIndex(index)) {
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

        if (whites.size() != 0) { state.append("W").append(String.join(",",whites)); }
        if (blacks.size() != 0) {
            if (whites.size() != 0) { state.append(":"); }
            state.append("B").append(String.join(",",blacks));
        }
        return state.toString();
    }

    public static boolean IsValidBoardState(String state) {
        String re = "^(?:W(?<whites>(K?(?:3[0-2]|[12][0-9]|[0-9]))(?:,K?(?:3[0-2]|[12][0-9]|[0-9]))*)(?::?(?=B))*)?(?:B(?<blacks>(K?(?:3[0-2]|[12][0-9]|[0-9]))(?:,K?(?:3[0-2]|[12][0-9]|[0-9]))*))?$";
        return state.length() != 0 && state.matches(re);
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
    public boolean setPiece(int index, Piece piece) {
        if (!isValidIndex(index)) {
            return false;
        }
        squares[index].piece = piece;
        return true;
    }
    public boolean setPiece(int index, Color color, Piece.PieceType pieceType) {
        return setPiece(index, new Piece(color, pieceType));
    }

    public boolean checkKingPromotion(int index, Piece piece) {
        if (piece == null || piece.pieceType == Piece.PieceType.King) {
            return false;
        }
        Point endPoint = toPoint(index);
        return piece.pieceType == Piece.PieceType.Man && ((endPoint.y == 0 && piece.color == Color.White) ||
                (endPoint.y == 7 && piece.color == Color.Black));
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
        setPiece(endIndex, movingPiece);
        setPiece(midIndex, null);
        setPiece(startIndex, null);
        boolean isSkip = midIndex > 0;

        // Crown king
        if (checkKingPromotion(endIndex, movingPiece)) {
            setPiece(endIndex, movingPiece.color, Piece.PieceType.King);
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
            Move temp = new Move(startIndex, end_index,true);
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

    public List<Integer> pointsToIndexes(List<Point> points) {
        List<Integer> indexes = new ArrayList<>();
        if (points != null) {
            for (Point point : points) {
                indexes.add(toIndex(point));
            }
        }
        return indexes;
    }

    public void printTemp(int startIndex, List<Point> points, int indent) {
        String baseStr = " ".repeat(indent*3);
        baseStr += indent+". From position "+startIndex+" : ";

        baseStr+=pointsToIndexes(points);
        System.out.println(baseStr);
    }
}

