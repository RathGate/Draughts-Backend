import checkers.board.Board;
import checkers.board.Color;
import checkers.board.Piece;
import checkers.logic.Game;
import checkers.logic.Move;
import checkers.logic.Notation;
import checkers.logic.Rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static checkers.board.Board.toBoard;

public class Main {
//    public static void main(String[] args) {
//        String state = "W18,14,19,21,23,24,K26,29,30,31,32:B1,2,3,4,6,7,9,10,11,12";
//
//        Game game = new Game();
//
//        game.setBoard(toBoard(state));
//
//        game.print();
//        List<Move> moves = game.getBoard().getCompleteSkips(4);
//        System.out.println(moves.size());
//        for (Move move : moves) {
//            System.out.println(move);
//        }
//        game.getBoard().print(true);
//        System.out.println(game.getBoard().getLegalMoves(Color.Black));
//        System.out.println(game.getBoard().toState());

//        String str = """
//                1. 11-15 23-18 2. 8-11 26-23 {Crescent Cross} 3. 10-14 30-26 4. 6-10 24-19 5.
//                                               15-24 27-20 {28-19 loses PP} 6. 4-8 {the popular book line is 12-16 28-24 4-8
//                                               22-17 8-12 32-28 = same} 32-27 {Perez' cook; 28-24 apparently goes to the
//                                               previous note} 7. 12-16 {seems to be the only move with any strength} 27-24 8.
//                                               8-12 22-17 {returning to the book line mentioned earlier} 9. 10-15 17-10 10.
//                                               7-14 26-22 1/2-1/2 {a very popular position} 1/2-1/2""";
//        str = str.replaceAll("\\{[^{}]*\\}", "").replaceAll("\\s+", " ").strip();
//
//        String re = "^(?<rounds>(?:(\\d+\\.\\s?)(?:(?:\\d{1,2}-\\d{1,2}|\\d{1,2}x\\d{1,2}(?:x\\d{1,2}){0,9}) ){1,2})+)(?<result>(\\*)|(\\d-\\d)|(1\\/2-1\\/2))?";
//
//        Pattern pattern = Pattern.compile(re);
//        Matcher matcher = pattern.matcher(str);
//        boolean success = matcher.find();
//        String[] rounds = success ? matcher.group("rounds").split("\\s?\\d+\\.\\s?") : null;
//        if (rounds != null && rounds.length != 0 && Objects.equals(rounds[0], "")) {
//            rounds = Arrays.copyOfRange(rounds, 1, rounds.length);
//        }
//        String result = success ? matcher.group("result") : null;
//
//        List<Move[]> records = new ArrayList<>();
//
//        if (rounds == null)  { return; }
//
//        for (int i = 0; i < rounds.length ; i++) {
//            String[] moves = rounds[i].split(" ");
//            for (String move : moves) {
//                System.out.println(Notation.toMove(move));
//            }
//        }
//
//        System.out.println("Résultat: "+result);
//    }

    public static void main(String[] args) {
        game_server.CheckersServer.main(args);
    }
}

