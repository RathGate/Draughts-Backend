package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import Server.ChessGameHandler.*;

public class Server {
    private static final int PORT_NUMBER = 3000;
    private static final BlockingQueue<Socket> playerQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT_NUMBER)) {
            System.out.println("Server is waiting for players...");

            while (true) {
                // Accept a new player
                Socket playerSocket = serverSocket.accept();
                System.out.println("Player connected!");

                // Add the player to the queue
                playerQueue.offer(playerSocket);

                // Try to match players in pairs
                matchPlayers();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void matchPlayers() {
        while (playerQueue.size() >= 2) {
            // Dequeue two players to form a match
            Socket player1 = playerQueue.poll();
            Socket player2 = playerQueue.poll();

            // Create a new thread to handle the chess game for the matched players
            ChessGameHandler gameHandler = new ChessGameHandler(player1, player2);
            new Thread(gameHandler).start();
        }
    }
}
