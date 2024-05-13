package checkers;

public class Square {
    public Piece piece;

    public Square() {
        this.piece = null;
    }
    public Square(Piece piece) {
        this.piece = piece;
    }

    @Override
    public String toString() {
        return piece != null ? piece.toString() : "  ";
    }
}