package game_server;
import checkers.board.Color;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CheckersServer extends WebSocketServer {
    private static final int PORT = 6969;

    private Queue<WebSocket> waitingQueue = new ConcurrentLinkedQueue<>();
    private Queue<WebSocket> playerQueue = new ConcurrentLinkedQueue<>();
    private Map<WebSocket, NetworkPlayer> players = new HashMap<>();
    private Map<WebSocket, CheckersGame> games = new HashMap<>();
    private DbConn dbConn = new DbConn();

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
        // TODO
//        playerQueue.offer(conn);
        waitingQueue.offer(conn);
        players.put(conn, new NetworkPlayer(Color.White, conn));
        matchPlayers();
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // when a player connection is closed, remove it from the queue,
        // close the game if it is in progress and notify the opponent
        System.out.println("Connection closed: " + conn.getRemoteSocketAddress());
        waitingQueue.remove(conn);
        playerQueue.remove(conn);
        players.remove(conn);
        CheckersGame game = games.get(conn);
        if (game != null) {
            game.game.setForfeit(game.getPlayer(conn).getColor());
            WebSocket opponent = game.getOpponent(conn);
            try {
                if (opponent != null) {
                    NetworkPlayer p = game.getPlayer(opponent);
                    JSONObject moveJson = new JSONObject();
                    moveJson.put("game", game.game.toJsonObject(p.getColor()));
                    String username = Objects.equals(game.getPlayer(game.getOpponent(p.socket)).username, "") ? "Anonymous" : game.getPlayer(game.getOpponent(p.socket)).username;
                    moveJson.put("opponent_username", username);
                    opponent.send(moveJson.toString());
                }
            } catch (Exception ignored) {};
            games.remove(game.getPlayer1());
            games.remove(game.getPlayer2());
            players.remove(game.getPlayer1());
            players.remove(game.getPlayer2());
            saveGame(game);
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println(message);
        // when a message is received, handle the move
        // if the game is over, remove both players from the game
        JSONObject obj = new JSONObject(message);
        CheckersGame game = games.get(conn);

        if (game == null) {
            NetworkPlayer player = players.get(conn);
            try {
                player.id = obj.getInt("id");
                player.username = obj.getString("username");
                if (!player.isComplete()) {
                    return;
                }
                waitingQueue.remove(conn);
                playerQueue.offer(conn);
                matchPlayers();
                int id = dbConn.insertIntoDatabase("matchmaking_logs", "user_id",
                        String.format("%d", players.get(conn).id));
            } catch (Exception ignored) {};
            return;
        }
        NetworkPlayer p = game.getPlayer(conn);
        game.handleMove(conn, obj.getString("move"));

        if (!game.game.isGameOver) {
            return;
        }

        saveGame(game);
        games.remove(game.getPlayer1());
        games.remove(game.getPlayer2());
        players.remove(game.getPlayer1());
        players.remove(game.getPlayer2());
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
            System.out.println(player1);
            System.out.println(player2);
            if (player1 != null && player2 != null) {
                startGame(player1, player2);
            }
        }
    }

    private void startGame(WebSocket player1, WebSocket player2) {
        // start a new game between two players
        System.out.println("Starting a new game between " + player1.getRemoteSocketAddress() + " and "
                + player2.getRemoteSocketAddress());

        System.out.println("----------");
        System.out.println(player1);
        System.out.println(player2);

        // initialize a new game
        CheckersGame game = new CheckersGame(players.get(player1), players.get(player2));
        games.put(player1, game);
        games.put(player2, game);

        System.out.println("----------");
        System.out.println(game.player1.socket + " "+game.player1.username);
        System.out.println(game.player2.socket + " "+game.player2.username);

        // send the initial game state to both players
        for (NetworkPlayer p : game.players) {
            JSONObject moveJson = new JSONObject();
            moveJson.put("game", game.game.toJsonObject(p.getColor()));
            String username = Objects.equals(game.getPlayer(game.getOpponent(p.socket)).username, "") ? "Anonymous" : game.getPlayer(game.getOpponent(p.socket)).username;
            moveJson.put("opponent_username", username);
            p.socket.send(moveJson.toString());
        }
    }

    public static void main(String[] args) {
        // start the checkers server
        CheckersServer server = new CheckersServer();
        server.start();
        System.out.println("Checkers server started on port: " + PORT);
    }

    public boolean saveGame(CheckersGame game) {
        System.out.printf("\"%s\", \"%s\", %d, %d%n", game.game.FEN, game.game.getMovesStr(), game.game.round, game.game.result.getScore().ordinal());
        int id = dbConn.insertIntoDatabase("games", "fen, history, rounds, result_id",
                String.format("\"%s\", \"%s\", %d, %d", game.game.FEN, game.game.getMovesStr(), game.game.round, game.game.result.getScore().ordinal()));
        if (id < 0) {
            return false;
        }
        for (NetworkPlayer p : game.players) {
            dbConn.insertIntoDatabase("game_players", "user_id, color_id, game_id",
                    String.format("%d, %d, %d", p.id, p.getColor().ordinal()+1, id));
            System.out.println(p.getColor().ordinal()+1);
        }
        return true;
    }

}
