package io.github.kirillbadin.tennisscoreboard.live;

import java.util.Optional;

public class TieBreakScore {

    private static final int WINNING_POINTS = 7;
    private static final int MINIMUM_DIFFERENCE = 2;

    private int firstSidePoints;
    private int secondSidePoints;

    public void addPoint(PlayerSide playerSide) {
        if (getWinner().isPresent()) {
            throw new IllegalStateException("Tiebreak already finished");
        }
        if (playerSide == PlayerSide.FIRST) {
            firstSidePoints++;
        } else {
            secondSidePoints++;
        }
    }

    public Optional<PlayerSide> getWinner(){
        if (firstSidePoints >= WINNING_POINTS && firstSidePoints - secondSidePoints >= MINIMUM_DIFFERENCE) {
            return Optional.of(PlayerSide.FIRST);
        }
        if (secondSidePoints >= WINNING_POINTS && secondSidePoints - firstSidePoints >= MINIMUM_DIFFERENCE) {
            return Optional.of(PlayerSide.SECOND);
        }
        return Optional.empty();
    }

    public int getPoints(PlayerSide side) {
        return side == PlayerSide.FIRST ? firstSidePoints : secondSidePoints;
    }
}
