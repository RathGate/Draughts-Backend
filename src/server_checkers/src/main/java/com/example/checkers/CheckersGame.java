package com.example.checkers;

import org.java_websocket.WebSocket;

public class CheckersGame {
    private WebSocket player1;
    private WebSocket player2;
    private int[][] board;

    public CheckersGame(WebSocket player1, WebSocket player2) {
        this.player1 = player1;
        this.player2 = player2;
        initializeBoard();
    }

    private void initializeBoard() {
        board = new int[8][8];
    }

    public void handleMove(WebSocket player, String move) {
        // Parse and validate the move, update the board, and notify players
        if (player == player1) {
            player2.send("Opponent's move: " + move);
        } else {
            player1.send("Opponent's move: " + move);
        }
    }

    public boolean isGameOver() {
        // game over conditions
        return false;
    }

    public WebSocket getPlayer1() {
        return player1;
    }

    public WebSocket getPlayer2() {
        return player2;
    }

    public WebSocket getOpponent(WebSocket player) {
        return player == player1 ? player2 : player1;
    }
}
