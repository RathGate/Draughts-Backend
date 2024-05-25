package com.example.checkers;

import netscape.javascript.JSObject;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class CheckersServer extends WebSocketServer {

    private static final int PORT = 3000;
    private Queue<WebSocket> playerQueue = new ConcurrentLinkedQueue<>();
    private Map<WebSocket, CheckersGame> games = new HashMap<>();

    public CheckersServer() {
        super(new InetSocketAddress(PORT));
    }

    @Override
    public void onStart() {
        System.out.println("Server started successfully!");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
        playerQueue.offer(conn);
        matchPlayers();
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
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
        ex.printStackTrace();
    }

    private void matchPlayers() {
        while (playerQueue.size() >= 2) {
            WebSocket player1 = playerQueue.poll();
            WebSocket player2 = playerQueue.poll();

            if (player1 != null && player2 != null) {
                startGame(player1, player2);
            }
        }
    }

    private void startGame(WebSocket player1, WebSocket player2) {
        System.out.println("Starting a new game between " + player1.getRemoteSocketAddress() + " and "
                + player2.getRemoteSocketAddress());

        Integer firstPlayerToMove = Math.random() < 0.5 ? 1 : 2;

        JSONObject startMessageP1 = new JSONObject();
        startMessageP1.put("type", "start");
        startMessageP1.put("playerId", 1);
        startMessageP1.put("color", "black");
        startMessageP1.put("opponent", "white");
        startMessageP1.put("firstMove", firstPlayerToMove);
        player1.send(startMessageP1.toString());

        JSONObject startMessageP2 = new JSONObject();
        startMessageP2.put("type", "start");
        startMessageP2.put("playerId", 2);
        startMessageP2.put("color", "white");
        startMessageP2.put("opponent", "black");
        startMessageP2.put("firstMove", firstPlayerToMove);
        player2.send(startMessageP2.toString());

        CheckersGame game = new CheckersGame(player1, player2);
        games.put(player1, game);
        games.put(player2, game);
    }

    public static void main(String[] args) {
        CheckersServer server = new CheckersServer();
        server.start();
        System.out.println("Checkers server started on port: " + PORT);
    }
}
