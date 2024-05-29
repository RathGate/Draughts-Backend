import checkers.board.Board;
import checkers.board.Color;
import checkers.board.Piece;
import checkers.logic.Game;
import checkers.logic.Move;
import checkers.logic.Notation;
import checkers.logic.Rules;
import game_server.CheckersServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static checkers.board.Board.toBoard;

public class Main {
    public static void main(String[] args) {

        CheckersServer.main(args);
    }

}

