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
import java.util.List;

public class Game {
    // Starting position in PDN (FEN)
    public String FEN;

    // Board
    private Board board;
    // Copy of the board updated at the end of each turn
    private Board saved_board;

    // Lists of moves done by the players
    public List<Move> moves = new ArrayList<>();
    // Keeps track of the current move if it is made
    // of successive jumps
    public Move lastSkipInTurn = null;

    // Players
    public Player playerWhite;
    public Player playerBlack;
    // Starts with Whites ?
    public boolean isWhiteTurn = true;

    // Game variables
    public int round = 1;
    public boolean isGameOver = false;
    public Result result = null;

    // Tie-linked variables
    public List<String> positions;
    public int consecutiveKingMoves = 0;

    public void print() {
        System.out.println(" --- GAME ---");
        System.out.println("Round : "+round);
        System.out.println("Current turn : "+(isWhiteTurn ? "White" : "Black"));
        System.out.println("IsGameOver : "+isGameOver);
        System.out.println(result);
        if (result != null) {
            System.out.println("Result : " + result.getScore() + " (" + result.getScoreComment() + ")");
        }
        System.out.println(moves);
        board.print(false);
    }

    public Game() {
        setBoard(new Board(), true);
        this.FEN = this.board.toState();
        playerWhite = new Player(checkers.board.Color.White);
        playerBlack = new Player(Color.Black);
        this.positions = new ArrayList<>();
        this.result = null;
    }

    public boolean makeMove(Point startPoint, Point endPoint) {
        if (startPoint == null || endPoint == null) {
            return false;
        }
        return makeMove(Board.toIndex(startPoint), Board.toIndex(endPoint));
    }
    public boolean makeMove(int startIndex, int endIndex) {
        if (isGameOver) {
            return false;
        }

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

        // Check if king moves in a row
        System.out.println("Is king "+movingPiece.isKing());
        System.out.println("Is Skip "+isSkip);
        if (movingPiece.isKing() && !isSkip) {
            consecutiveKingMoves++;
        } else {
            consecutiveKingMoves = 0;
        }

        // Crown king
        if (board.checkKingPromotion(endIndex, movingPiece)) {
            board.setPiece(endIndex, movingPiece.color, Piece.PieceType.King);
        }

        // Record move;
        recordMove(startIndex, endIndex, isSkip, saved_board);
        if (isSkip && !board.getPossibleSkips(endIndex).isEmpty()) {
            switchTurn = false;
        }
        if (switchTurn) {
            if (!isWhiteTurn) {
                round++;
            }
            if (saved_board.getCompleteSkips(startIndex, endIndex).size() > 0) {
                lastSkipInTurn.isAmbiguous = true;
            }
            lastSkipInTurn = null;
            saved_board = this.board.copy();
            isWhiteTurn = !isWhiteTurn;
            this.positions.add(board.toState());
            this.isGameOver = checkIsGameOver();
        }
        return true;
    }

    public void setBoard(Board newBoard, boolean isFEN) {
        this.board = newBoard;
        this.saved_board = newBoard.copy();
        if (isFEN) {
            this.FEN = this.toState();
        }
    }
    public Board getBoard() {
        return this.board;
    }

    public void recordMove(int startIndex, int endIndex, boolean isSkip, Board saved_board)  {
        if (!isSkip) {
            moves.add(new Move(startIndex, endIndex, false));
            return;
        }
        if (lastSkipInTurn != null) {
            lastSkipInTurn.addStepAfter(endIndex);
            return;
        }

        Move move = new Move(startIndex, endIndex, true, null, false);
        moves.add(move);
        lastSkipInTurn = move;
    }

    public Color getCurrentPlayerColor() {
        return isWhiteTurn ? Color.White : Color.Black;
    }
    public Color getOtherPlayerColor(Color color) {
        return color == Color.White ? Color.Black : Color.White;
    }

    public JSONObject toJsonObject(Color player_color) {
        JSONObject jsonObject = toJsonObject();
        jsonObject.put("player_color", player_color);
        if (getCurrentPlayerColor() == player_color) {
            jsonObject.put("legal_moves", getBoard().getLegalMovesStr(player_color));
        }
        return jsonObject;
    }

    public JSONObject toJsonObject() {
        System.out.println("Consecutive kings : "+consecutiveKingMoves);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fen", this.FEN);
        jsonObject.put("board", this.toState());
        jsonObject.put("round", round);
        JSONArray jsonHistory = new JSONArray();
        for (Move move : moves) {
            jsonHistory.put(move.toString(true));
        }
        jsonObject.put("history", jsonHistory);
        jsonObject.put("history_str", getMovesStr());
        if (moves.size() > 0) {
            List<Integer> last_move = moves.get(moves.size() - 1).getSteps(true);
            jsonObject.put("last_move", new JSONArray(last_move));
        }

        jsonObject.put("current_turn", getCurrentPlayerColor().toString());

        if (result == null) {
            return jsonObject;
        }

        JSONObject jsonResult = new JSONObject();
        jsonResult.put("score", result.getScore().toString());
        String comment = result.getScoreComment() != Result.ScoreComment.None ? result.getScoreComment().toString() : "";
        jsonResult.put("score_comment", comment);

        jsonObject.put("result", jsonResult);
        return jsonObject;
    }
    public String toJSONString() {
        return this.toJsonObject().toString();
    }

    public List<String> getMovesList() {
        List<String> movesList = new ArrayList<>();
        for (Move move : moves) {
            String moveStr = move == null ? "" : move.toString(true);
            movesList.add(moveStr);
        }
        return movesList;
    }

    public String getMovesStr() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.moves.size(); i++) {
            if (i % 2 == 0) {
                stringBuilder.append(i / 2 + 1).append(".");
            }
            if (moves.get(i) != null) {
                stringBuilder.append(moves.get(i).toString(false)).append(" ");
            }
        }
        return stringBuilder.toString();
    }

    public String toState() {
        if (board == null) {
            return null;
        }
        String c = getCurrentPlayerColor() == Color.White ? "W:" : "B:";
        return c + board.toState();
    }

    public boolean checkIsGameOver() {
        if (this.board == null || this.board.findPieces().size() == 0) {
            return true;
        }
        Color current_player = getCurrentPlayerColor();

        // Current Player has no pieces left
        if (this.board.findPieces(getCurrentPlayerColor()).size() == 0) {
            Result.Score score = current_player == Color.White ? Result.Score.Black : Result.Score.White;
            this.result = new Result(score, Result.ScoreComment.NO_PIECES);
            return true;
        }

        // Current Player has no legal moves left
        if (this.board.getLegalMoves(getCurrentPlayerColor()).size() == 0) {
            Result.Score score = current_player == Color.White ? Result.Score.Black : Result.Score.White;
            this.result = new Result(score, Result.ScoreComment.NO_LEGAL_MOVES);
            return true;
        }

        // Repetition of same pattern three times:
        // Todo?

        // 20 king moves in a row
        if (this.consecutiveKingMoves >= 20) {
            this.result = new Result(Result.Score.Tie, Result.ScoreComment.CONSECUTIVE_KINGS);
            return true;
        }
        return false;
    }

}
