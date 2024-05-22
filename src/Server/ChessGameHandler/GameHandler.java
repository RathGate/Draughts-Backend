package Server.ChessGameHandler;

import java.io.BufferedReader;
import java.io.IOException;package Server.GameHandler;

import org.java_websocket.WebSocket;

import java.io.IOException;
import java.io.PrintWriter;

public class GameHandler implements Runnable {
    private final WebSocket player1;
    private final WebSocket player2;

    public GameHandler(WebSocket player1, WebSocket player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    private void endMatch(PrintWriter outPlayer1, PrintWriter outPlayer2) throws IOException {
        outPlayer1.println("Match ended!");
        outPlayer2.println("Match ended!");
    }

    @Override
    public void run() {
        try {
            // game logic

            player1.send("Match started! You are player 1.");
            player2.send("Match started! You are player 2.");

            // Handle messages from players
            while (true) {
                String messageFromPlayer1 = player1.receiveMessage();
                String messageFromPlayer2 = player2.receiveMessage();
                // Handle messages from players if needed
                if (messageFromPlayer1.equals("QUIT") || messageFromPlayer2.equals("QUIT")) {
                    endMatch(player1, player2);
                    break;
                }
            }

            // end of the match
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChessGameHandler implements Runnable {
    private final Socket player1;
    private final Socket player2;

    public ChessGameHandler(Socket player1, Socket player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    private void endMatch(PrintWriter outPlayer1, PrintWriter outPlayer2) {
        outPlayer1.println("Match ended!");
        outPlayer2.println("Match ended!");
    }

    @Override
    public void run() {
        try (
                BufferedReader inPlayer1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
                PrintWriter outPlayer1 = new PrintWriter(player1.getOutputStream(), true);
                BufferedReader inPlayer2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
                PrintWriter outPlayer2 = new PrintWriter(player2.getOutputStream(), true)) {

            // game logic

            outPlayer1.println("Match started! You are player 1.");
            outPlayer2.println("Match started! You are player 2.");

            if (inPlayer1.readLine().equals("QUIT") || inPlayer2.readLine().equals("QUIT")) {
                endMatch(outPlayer1, outPlayer2);
            }

            // end of the match

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
