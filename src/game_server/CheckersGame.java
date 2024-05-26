package game_server;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

public class CheckersGame {
    private WebSocket player1;
    private WebSocket player2;

    public CheckersGame(WebSocket player1, WebSocket player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public void handleMove(WebSocket player, String move) {
        JSONObject moveJson = new JSONObject();
        moveJson.put("type", "move");
        moveJson.put("move", move);
        if (player == player1) {
            player2.send(moveJson.toString());

        } else {
            player1.send(moveJson.toString());
        }
    }

    public boolean isGameOver() {
        // game over conditions
        return false;
    }

    public WebSocket getPlayer1() {
        return player1;
    }

    public WebSocket getPlayer2() {
        return player2;
    }

    public WebSocket getOpponent(WebSocket player) {
        return player == player1 ? player2 : player1;
    }
}
