package checkers.logic;

import checkers.board.Board;

import java.util.ArrayList;
import java.util.List;

public class Move {
    public boolean isSkip = false;
    public int startIndex;
    public int endIndex;
    public List<Integer> skipSteps = new ArrayList<>();
    boolean isAmbiguous = false;

    public Move(int startIndex, int endIndex) {
        this(startIndex, endIndex, false, null,false);
    }

    public Move(int startIndex, int endIndex, boolean isSkip) {
        this(startIndex, endIndex, isSkip, null, false);
    }
    public Move(int startIndex, int endIndex, boolean isSkip, List<Integer> intSteps, boolean isAmbiguous) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.isSkip = isSkip;
        if (isSkip) {
            this.skipSteps = intSteps == null ? new ArrayList<>() : intSteps;
            this.isAmbiguous = isAmbiguous;
        }
    }

    public void addStepAfter(int newEndIndex) {
        if (!Board.isValidIndex(newEndIndex) || !this.isSkip) {
            return;
        }
        skipSteps.add(this.endIndex);
        this.endIndex = newEndIndex;
    }
    public void addStepBefore(int newStartIndex) {
        if (!Board.isValidIndex(newStartIndex) || !this.isSkip) {
            return;
        }
        skipSteps.add(0,this.startIndex);
        this.startIndex = newStartIndex;
    }

    @Override
    public String toString() {
        return this.toString(false);
    }
    public String toString(boolean forceAmbiguous) {
        StringBuilder result = new StringBuilder(String.valueOf(startIndex+1));

        if (!isSkip) {
            result.append("-").append(endIndex+1);
            return result.toString();
        }
        result.append("x");
        if (isAmbiguous || forceAmbiguous) {
            for (int skipStep : skipSteps) {
                result.append(skipStep + 1).append("x");
            }
        }
        result.append(endIndex+1);
        return result.toString();
    }

    public Move copy() {
        return new Move(this.startIndex, this.endIndex, this.isSkip, this.skipSteps, this.isAmbiguous);
    }

    public void testPrint() {
        System.out.println("Start: "+this.startIndex + " | End: "+this.endIndex+" | Middle: "+skipSteps +" | IsAmbiguous: "+this.isAmbiguous);
    }

    public List<Integer> getSteps(boolean increment) {
        int incr = increment ? 1 : 0;
        List<Integer> result = new ArrayList<>();
        result.add(startIndex+incr);
        for (int i : skipSteps) {
            result.add(i+incr);
        }
        result.add(endIndex+incr);
        return result;
    }

}
