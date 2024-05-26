package checkers.logic;

import checkers.board.Board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        return new Move(start, end, isSkip, skipSteps);
    }
    public static String sanitizeString(String str) {
        return str.replaceAll("\\{[^{}]*\\}", "").replaceAll("\\s+|[?!]", " ").strip();
    }
}