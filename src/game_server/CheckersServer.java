package game_server;

import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import game_server.DbConn;

public class CheckersServer extends WebSocketServer {

    private static final int PORT = 6969;

    private Queue<WebSocket> playerQueue = new ConcurrentLinkedQueue<>();
    private Map<WebSocket, CheckersGame> games = new HashMap<>();

    // private DbConn dbConn = new DbConn();

    public CheckersServer() {
        // initialize a websocket server with the port
        super(new InetSocketAddress(PORT));
    }

    @Override
    public void onStart() {
        System.out.println("Server started successfully!");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // when a new connection is opened, add it to the queue
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
        // dbConn.insertIntoDatabase("matchmaking_log", "nom, enter_queue",
        // "'" + conn.getRemoteSocketAddress().toString() + "', CURRENT_TIMESTAMP");
        playerQueue.offer(conn);
        matchPlayers();
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // when a player connection is closed, remove it from the queue,
        // close the game if it is in progress and notify the opponent
        System.out.println("Connection closed: " + conn.getRemoteSocketAddress());
        playerQueue.remove(conn);
        CheckersGame game = games.remove(conn);
        if (game != null) {
            WebSocket opponent = game.getOpponent(conn);
            if (opponent != null) {
                opponent.send("Opponent disconnected, game over.");
            }
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // when a message is received, handle the move
        // if the game is over, remove both players from the game
        CheckersGame game = games.get(conn);
        if (game != null) {
            game.handleMove(conn, message);
            if (game.isGameOver()) {
                games.remove(game.getPlayer1());
                games.remove(game.getPlayer2());
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        // log the error
        ex.printStackTrace();
    }

    private void matchPlayers() {
        // match players in the queue, two at a time
        while (playerQueue.size() >= 2) {
            WebSocket player1 = playerQueue.poll();
            WebSocket player2 = playerQueue.poll();

            if (player1 != null && player2 != null) {
                startGame(player1, player2);
            }
        }
    }

    private void startGame(WebSocket player1, WebSocket player2) {
        // start a new game between two players
        System.out.println("Starting a new game between " + player1.getRemoteSocketAddress() + " and "
                + player2.getRemoteSocketAddress());

        // initialize a new game
        CheckersGame game = new CheckersGame(player1, player2);
        games.put(player1, game);
        games.put(player2, game);

        // send the initial game state to both players
        for (NetworkPlayer p : game.players) {
            JSONObject moveJson = new JSONObject();
            moveJson.put("game", game.game.toJsonObject(p.getColor()));
            p.socket.send(moveJson.toString());
        }
    }

    public static void main(String[] args) {
        // start the checkers server
        CheckersServer server = new CheckersServer();
        server.start();
        System.out.println("Checkers server started on port: " + PORT);
    }
}
