package game_server;

import checkers.board.Color;
import checkers.logic.Game;
import checkers.logic.Move;
import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.json.JSONObject;

import java.awt.image.AffineTransformOp;

public class CheckersGame {
    public NetworkPlayer[] players;
    public NetworkPlayer player1;
    public NetworkPlayer player2;
    public Game game;


    public CheckersGame(WebSocket player1, WebSocket player2) {
        this.player1 = new NetworkPlayer(Color.White, player1);
        this.player2 = new NetworkPlayer(Color.Black, player2);
        this.players = new NetworkPlayer[]{this.player1, this.player2};
        this.game = new Game();
    }

    public void handleMove(WebSocket player, String move) {
        boolean isValid;
        try {
            String[] positions = move.split(" ");
            isValid = game.makeMove(Integer.parseInt(positions[0]), Integer.parseInt(positions[1]));
        } catch (Exception e) {
            isValid = false;
        }
        if (!isValid) {
            JSONObject moveJson = new JSONObject();
            moveJson.put("is_move_valid", false);
            player.send(moveJson.toString());
            return;
        }

        for (NetworkPlayer p : this.players) {
            JSONObject moveJson = new JSONObject();
//            moveJson.put("is_move_valid", true);
            moveJson.put("game", game.toJsonObject(p.getColor()));
//            moveJson.put("player_color", p.getColor());
//            if (game.getCurrentPlayerColor() == p.getColor()) {
//                moveJson.put("legal_moves", game.getBoard().getLegalMovesStr(p.getColor()));
//            }
            p.socket.send(moveJson.toString());
        }
    }

    public boolean isGameOver() {
        // game over conditions
        return false;
    }

    public WebSocket getPlayer1() {
        return player1.socket;
    }

    public WebSocket getPlayer2() {
        return player2.socket;
    }

    public WebSocket getOpponent(WebSocket player) {
        return player == player1.socket ? player2.socket : player1.socket;
    }
}
