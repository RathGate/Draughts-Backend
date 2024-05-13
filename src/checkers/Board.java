package checkers;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Board {
    public Square[] squares = new Square[32];

    public Board() {
        EmptyBoard();

        for (int i = 0; i < 8; i++) {
            squares[i].piece = new Piece(Color.Black);
            squares[31-i].piece = new Piece(Color.White);
        }
    }

    public void EmptyBoard() {
        for (int i = 0; i < squares.length; i++) {
            squares[i] = new Square();
        }
    }

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

    public List<Move> getLegalMoves(Color color) {
        List<Move> legalMoves = new ArrayList<>();
        List<Point> checkers = findCheckerPositions(color);

        if (checkers.size() == 0) {
            return legalMoves;
        }

        for (Point checker : checkers) {
            List<Point> skips = getPossibleSkips(checker);
            int index = toIndex(checker);
            for (Point skip : skips) {
                Move m = new Move(index, toIndex(skip), Move.MoveType.Skip);
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

    public List<Point> findCheckerPositions() {
        List<Point> points = new ArrayList<>();

        for (int i = 0; i < 32; i++) {
            if (getPieceAt(i) != null) {
                points.add(toPoint(i));
            }
        }
        return points;
    }
    public List<Point> findCheckerPositions(Color color) {
        List<Point> points = new ArrayList<>();

        for (int i = 0; i < 32; i++) {
            Piece piece = getPieceAt(i);
            if (piece != null && piece.color == color) {
                points.add(toPoint(i));
            }
        }
        return points;
    }
    public List<Point> findCheckerPositions(Piece.PieceType pieceType) {
        List<Point> points = new ArrayList<>();

        for (int i = 0; i < 32; i++) {
            Piece piece = getPieceAt(i);
            if (piece != null && piece.pieceType == pieceType) {
                points.add(toPoint(i));
            }
        }
        return points;
    }
    public List<Point> findCheckerPositions(Color color, Piece.PieceType pieceType) {
        List<Point> points = new ArrayList<>();

        for (int i = 0; i < 32; i++) {
            Piece piece = getPieceAt(i);
            if (piece != null && piece.color == color && piece.pieceType == pieceType) {
                points.add(toPoint(i));
            }
        }
        return points;
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
    }
}
