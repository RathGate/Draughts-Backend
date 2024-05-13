package checkers;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Move {
    public enum MoveType {
        Move, Skip
    }
    public int startIndex;
    public int endIndex;
    public MoveType moveType;
    public List<Integer> interSteps = new ArrayList<>();

    public Move(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.moveType = MoveType.Move;
    }
    public Move(int startIndex, int endIndex, MoveType moveType) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.moveType = moveType;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(String.valueOf(startIndex));

        if (moveType == MoveType.Move) {
            result.append("-").append(endIndex);
            return result.toString();
        }
        result.append("x");
        for (int interStep : interSteps) {
            result.append(interStep).append("x");
        }
        result.append(endIndex);
        return result.toString();
    }
}
