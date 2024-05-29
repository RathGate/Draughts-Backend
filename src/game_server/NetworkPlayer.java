package game_server;

import checkers.Player;
import checkers.board.Color;
import org.java_websocket.WebSocket;

import java.util.Objects;

public class NetworkPlayer extends Player {
    public WebSocket socket;
    public String username = "";
    public int id = -1;
    public NetworkPlayer(Color color, WebSocket socket) {
        super(color);
        this.socket = socket;
    }

    @Override
    public String toString() {
        return ("Player "+this.getColor()+" | " + username+ " | ");
    }

    public boolean isComplete() {
        return !Objects.equals(username, "") && id > 0;
    }

}
