package checkers.logic;
public class Result {
    public enum Score {
        None, White, Black, Tie
    }
    public enum ScoreComment {
        None,
        NO_PIECES,
        NO_LEGAL_MOVES,
        CONSECUTIVE_KINGS,
        BOARD_REPETITION,
        FORFEIT
    }

    private Score score;
    private ScoreComment scoreComment;

    public Result(Score score) {
        this(score, ScoreComment.None);
    }
    public Result(Score score, ScoreComment scoreComment) {
        this.score = score;
        this.scoreComment = scoreComment;
    }

    public Score getScore() {
        return this.score;
    }
    public void setScore(Score newScore) {
        this.score = newScore;
    }
    public ScoreComment getScoreComment() {
        return this.scoreComment;
    }
    public void setScoreComment(ScoreComment newScoreComment) {
        this.scoreComment = newScoreComment;
    }
}
