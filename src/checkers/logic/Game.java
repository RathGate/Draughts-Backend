package checkers.logic;

import checkers.Player;
import checkers.board.Board;
import checkers.board.Color;
import checkers.board.Piece;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game {
    public enum Result {
        None, WHITE_WIN, BLACK_WIN, TIE
    }

    public List<Move> moves = new ArrayList<>();
    public int lastSkipIndex = -1;
    public Move lastSkipInTurn = null;
    private Board board;
    private Board saved_board;
    public Player playerWhite;
    public Player playerBlack;
    public boolean isWhiteTurn = true;
    public int round = 1;
    public Result result = Result.None;

    public void print() {
        System.out.println(" --- GAME ---");
        System.out.println("Round : "+round);
        System.out.println("Current turn : "+(isWhiteTurn ? "White" : "Black"));
        System.out.println(moves);
        board.print(false);
    }

    public Game() {
        setBoard(new Board());
        playerWhite = new Player(checkers.board.Color.White);
        playerBlack = new Player(Color.Black);
    }

    public Game(Player player1, Player player2) {
        setBoard(new Board());
        this.isWhiteTurn = false;
    }


    public boolean makeMove(Point startPoint, Point endPoint) {
        if (startPoint == null || endPoint == null) {
            return false;
        }
        return makeMove(Board.toIndex(startPoint), Board.toIndex(endPoint));
    }
    public boolean makeMove(int startIndex, int endIndex) {
        boolean switchTurn = true;
        startIndex -= 1;
        endIndex -= 1;
        if (!Rules.isValidMove(this, startIndex, endIndex)) {
            return false;
        }

        Board copy = board.copy();

        Piece movingPiece = board.getPieceAt(startIndex);
        int midIndex = Board.getMiddleIndex(startIndex, endIndex);
        board.setPiece(endIndex, movingPiece);
        board.setPiece(midIndex, null);
        board.setPiece(startIndex, null);
        boolean isSkip = midIndex > 0;

        // Crown king
        if (board.checkKingPromotion(endIndex, movingPiece)) {
            board.setPiece(endIndex, movingPiece.color, Piece.PieceType.King);
            System.out.println("Crowned king !");
        }

        // Record move;
        recordMove(startIndex, endIndex, isSkip);

        if (isSkip && !board.getPossibleSkips(endIndex).isEmpty()) {
            switchTurn = false;
        }
        if (switchTurn) {
            if (!isWhiteTurn) {
                round++;
            }
            isWhiteTurn = !isWhiteTurn;
            lastSkipIndex = -1;
            lastSkipInTurn = null;
        }
        return true;
    }

    public void endPlayerRound() {

    }

    public void setBoard(Board newBoard) {
        this.board = newBoard;
        this.saved_board = newBoard;
    }
    public Board getBoard() {
        return this.board;
    }

    public void recordMove(int startIndex, int endIndex, boolean isSkip)  {
        if (!isSkip) {
            moves.add(new Move(startIndex, endIndex, false));
            return;
        }

        if (lastSkipInTurn != null) {
            lastSkipInTurn.addStepAfter(endIndex);
            return;
        }

        Move move = new Move(startIndex, endIndex, true);
        moves.add(move);
        lastSkipInTurn = move;
    }

    public Color getCurrentPlayerColor() {
        return isWhiteTurn ? Color.White : Color.Black;
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("round", round);
        jsonObject.put("board", this.getBoard().toState());
        JSONArray jsonMoves = new JSONArray();
        for (Move move : moves) {
            jsonMoves.put(move.toString());
        }
        jsonObject.put("moves", jsonMoves);
        jsonObject.put("current_turn", getCurrentPlayerColor().toString());
        return jsonObject;
    }
    public String toJSONString() {
        return this.toJsonObject().toString();
    }


}
