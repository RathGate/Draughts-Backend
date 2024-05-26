package game_server;

import checkers.Player;
import checkers.board.Color;
import org.java_websocket.WebSocket;

public class NetworkPlayer extends Player {
    public WebSocket socket;
    public NetworkPlayer(Color color, WebSocket socket) {
        super(color);
        this.socket = socket;
    }
}
