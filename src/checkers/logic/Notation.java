package checkers.logic;

import checkers.board.Board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Notation {
    public String regexBlackFirst = "";
    public static String moveRegex = "(?:\\d{1,2}-\\d{1,2}|\\d{1,2}x\\d{1,2}(?:x\\d{1,2}){0,9})";

    public static Move toMove(String str) {
        str = sanitizeString(str);

        // Matching regex
        Pattern pattern = Pattern.compile(moveRegex);
        Matcher matcher = pattern.matcher(str);
        if (!matcher.find()) { return null; }

        // Check for skip
        boolean isSkip = str.contains("x");
        int start = -1;
        int end = -1;
        List<Integer> skipSteps = new ArrayList<>();

        List<String> movesStr = Arrays.asList(str.split("[x-]"));
        if (movesStr.size() < 2 || (!isSkip && movesStr.size() > 2)) {
            return null;
        }

        for (int i = 0; i < movesStr.size(); i++) {
            int temp = 0;
            try {
                temp = Integer.parseInt(movesStr.get(i)) - 1;
            } catch (Exception e) {
                System.out.println("Impossible to parse : " + movesStr.get(i));
                return null;
            }
            if (i == 0) {
                start = temp;
                continue;
            } else if (i == movesStr.size() - 1) {
                end = temp;
                continue;
            }
            skipSteps.add(temp);
        }
        return new Move(start, end, isSkip, skipSteps, false);
    }
    public static String sanitizeString(String str) {
        return str.replaceAll("\\{[^{}]*\\}", "").replaceAll("\\s+|[?!]", " ").strip();
    }

    public static String toPDN(Game game) {
        return toPDN(game, "", "");
    }

    public static String toPDN(Game game, String white_username,String black_username) {
        StringBuilder pdn = new StringBuilder();
        if (!Objects.equals(black_username, "") && !Objects.equals(white_username, "")) {
            pdn.append("[White \"").append(white_username).append("\"]\n");
            pdn.append("[Black \"").append(black_username).append("\"]\n");
        }
        pdn.append("[GameType \"21\"]\n");
        String score = game.result != null ? game.result.getScoreStr() : "";
        if (!Objects.equals(score, "")) {
            pdn.append("[Result \"").append(score).append("\"]\n");
        }
        pdn.append("[FEN \"").append(game.FEN).append("\"]\n");
        pdn.append("\n");

        pdn.append(game.getMovesStr());
        if (!Objects.equals(score, "")) {
            pdn.append("* ").append(score);
        }
        pdn.append("\n");
        return pdn.toString();
    }

}