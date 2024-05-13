package checkers;
public class Piece {
    public enum PieceType {
        Man,
        King
    }
    public Color color;
    public PieceType pieceType;

    public Piece(Color color) {
        this.color = color;
        this.pieceType = PieceType.Man;
    }

    public Piece(Color color, PieceType pieceType) {
        this.color = color;
        this.pieceType = pieceType;
    }

    public boolean isKing() {
        return pieceType == PieceType.King;
    }
    public boolean isWhite() {
        return color == Color.White;
    }

    @Override
    public String toString() {
        String str = "";
        str += color == Color.White ? "W" : "B";
        str += pieceType == PieceType.Man ? "M":"K";

        return str;
    }
}
