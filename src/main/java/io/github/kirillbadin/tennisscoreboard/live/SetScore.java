package io.github.kirillbadin.tennisscoreboard.live;

import java.util.Optional;

public class SetScore {
    private final static int WINNING_SET_GAMES = 6;
    private static final int TIEBREAK_GAMES = 6;

    private GameScore gameScore = new GameScore();
    private TieBreakScore tieBreakScore;
    private int firstGame;
    private int secondGame;


    public void addPoint(PlayerSide side) {
        if (isFinished()) {
            throw new IllegalStateException("Set already finished");
        }
        if (tieBreakScore != null) {
            tieBreakScore.addPoint(side);
            tieBreakScore.getWinner().ifPresent(this::handleGameWin);
        } else {
            gameScore.addPoint(side);
            gameScore.getWinner().ifPresent(this::handleGameWin);
        }
    }

    private void handleGameWin(PlayerSide winnerSide) {
        if (winnerSide == PlayerSide.FIRST) {
            firstGame++;
        } else {
            secondGame++;
        }

        if (firstGame == TIEBREAK_GAMES && secondGame == TIEBREAK_GAMES) {
            tieBreakScore = new TieBreakScore();
        } else {
            gameScore = new GameScore();
        }
    }

    public Optional<PlayerSide> getWinner() {
        if (tieBreakScore != null) {
            return tieBreakScore.getWinner();
        }
        if (firstGame >= WINNING_SET_GAMES && firstGame - secondGame >= 2) {
            return Optional.of(PlayerSide.FIRST);
        }
        if (secondGame >= WINNING_SET_GAMES && secondGame - firstGame >= 2) {
            return Optional.of(PlayerSide.SECOND);
        }
        return Optional.empty();
    }

    public boolean isFinished() {
        return getWinner().isPresent();
    }

    public int getGames(PlayerSide side) {
        return side == PlayerSide.FIRST ? firstGame : secondGame;
    }

    public GamePoint getGamePoint(PlayerSide side) {
        return gameScore.getScoreFor(side);
    }

    public Integer getTieBreakPoints(PlayerSide side) {
        if (tieBreakScore == null || tieBreakScore.getWinner().isPresent()) {
            return null;
        }
        return tieBreakScore.getPoints(side);
    }
}
