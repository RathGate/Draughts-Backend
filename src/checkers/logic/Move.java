package checkers.logic;

import checkers.board.Board;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Move {
    public boolean isSkip = false;
    public boolean isAmbiguous = true;
    public int startIndex;
    public int endIndex;
    public List<Integer> skipSteps = new ArrayList<>();

    public Move(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public Move(int startIndex, int endIndex, boolean isSkip) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.isSkip = isSkip;
    }
    public Move(int startIndex, int endIndex, boolean isSkip, boolean isAmbiguous) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.isSkip = isSkip;
        if (this.isSkip) { this.isAmbiguous = isAmbiguous; }
    }

    public void addStep(int newEndIndex) {
        if (!Board.isValidIndex(newEndIndex) || !this.isSkip) {
            return;
        }
        skipSteps.add(this.endIndex);
        this.endIndex = newEndIndex;
    }
    public void addStep(int newEndIndex, boolean isAmbiguous) {
        addStep(newEndIndex);
        this.isAmbiguous = isAmbiguous;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(String.valueOf(startIndex));

        if (!isSkip) {
            result.append("-").append(endIndex);
            return result.toString();
        }
        result.append("x");
        if (isAmbiguous) {
            for (int skipStep : skipSteps) {
                result.append(skipStep).append("x");
            }
        }
        result.append(endIndex);
        return result.toString();
    }
}
