package game_server;

import checkers.board.Color;
import checkers.logic.Game;
import org.java_websocket.WebSocket;
import org.json.JSONObject;

import java.util.Objects;

public class NetworkGame {
    public NetworkPlayer[] players;
    public NetworkPlayer player1;
    public NetworkPlayer player2;
    public Game game;

    public NetworkGame(NetworkPlayer player1, NetworkPlayer player2) {
        // initialize the game with two players
        this.player1 = player1;
        player1.setColor(Color.White);
        this.player2 = player2;
        player2.setColor(Color.Black);
        this.players = new NetworkPlayer[] { this.player1, this.player2 };
        this.game = new Game();
    }

    public void handleMove(WebSocket player, String move) {
        // verify the move, if not valid, send a message to the player
        boolean isValid;
        try {
            String[] positions = move.split(" ");
            isValid = game.makeMove(Integer.parseInt(positions[0]), Integer.parseInt(positions[1]));
        } catch (Exception e) {
            isValid = false;
        }
        if (!isValid) {
            return;
        }

        // send the updated game state to both players
        for (NetworkPlayer p : this.players) {
            JSONObject moveJson = new JSONObject();
            moveJson.put("game", game.toJsonObject(p.getColor()));
            String username = Objects.equals(getPlayer(getOpponent(p.socket)).username, "") ? "Anonymous" : getPlayer(getOpponent(p.socket)).username;
            moveJson.put("opponent_username", username);
            p.socket.send(moveJson.toString());
        }
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
    public NetworkPlayer getPlayer(WebSocket player) {
        for (NetworkPlayer p : players) {
            if (p.socket == player) { return p; }
        }
        return null;
    }
    public NetworkPlayer getPlayer(Color color) {
        for (NetworkPlayer p : players) {
            if (p.getColor() == color) { return p; }
        }
        return null;
    }

}
