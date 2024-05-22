package Server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server extends WebSocketServer {
    private static final int PORT_NUMBER = 3000;
    private static final BlockingQueue<WebSocket> playerQueue = new LinkedBlockingQueue<>();

    // db connection
    private static final String DB_URL = "";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    public Server() {
        super(new InetSocketAddress(PORT_NUMBER));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
        // Add the player to the queue
        playerQueue.offer(conn);
        // Try to match players in pairs
        matchPlayers();
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + conn.getRemoteSocketAddress());
        // Remove the player from the queue if disconnected
        playerQueue.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Message received from client: " + message);
        // Handle messages from clients if needed
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    private static void matchPlayers() {
        while (playerQueue.size() >= 2) {
            // Dequeue two players to form a match
            WebSocket player1 = playerQueue.poll();
            WebSocket player2 = playerQueue.poll();
            // Create a new thread to handle the chess game for the matched players
            GameHandler gameHandler = new GameHandler(player1, player2);
            new Thread(gameHandler).start();
        }
    }

    // db connection
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to the database");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
        System.out.println("Server started on port " + PORT_NUMBER);
    }
}
