package checkers.board;

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


    public Square(Square square) {
        this.piece = square.piece == null ? null : new Piece(square.piece.color, square.piece.pieceType);
    }
}