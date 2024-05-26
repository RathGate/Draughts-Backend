package game_server;

import checkers.board.Color;
import checkers.logic.Game;
import org.java_websocket.WebSocket;
import org.json.JSONObject;

public class CheckersGame {
    public NetworkPlayer player1;
    public NetworkPlayer player2;
    public Game game;

    public CheckersGame(WebSocket player1, WebSocket player2) {
        this.player1 = new NetworkPlayer(Color.White, player1);
        this.player2 = new NetworkPlayer(Color.Black, player2);
        this.game = new Game();
    }

    public void handleMove(WebSocket player, String move) {
        JSONObject moveJson = new JSONObject();
        boolean isValid;
        try {
            String[] positions = move.split(" ");
            isValid = game.makeMove(Integer.parseInt(positions[0]), Integer.parseInt(positions[1]));
        } catch (Exception e) {
            isValid = false;
        }
        if (!isValid) {
            moveJson.put("is_move_valid", false);
            player.send(moveJson.toString());
            return;
        }
        moveJson.put("is_move_valid", true);
        moveJson.put("game", game.toJSONString());
        moveJson.put("current_player", player1.getColor());
        player1.socket.send(moveJson.toString());
        moveJson.put("current_player", player2.getColor());
        player2.socket.send(moveJson.toString());

        game.print();
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
