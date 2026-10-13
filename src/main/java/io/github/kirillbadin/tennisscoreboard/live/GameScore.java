package io.github.kirillbadin.tennisscoreboard.live;

import java.util.Optional;

public class GameScore {
    private GamePoint firstSidePoints = GamePoint.LOVE;
    private GamePoint secondSidePoints = GamePoint.LOVE;
    private PlayerSide gameWinner;

    public void addPoint(PlayerSide side){
        if(gameWinner != null){
            throw new IllegalStateException("Game already finished");
        }

        GamePoint winnerPoints = getScoreFor(side);
        GamePoint opponentPoints = getScoreFor(side.opposite());

        if (winnerPoints == GamePoint.ADVANTAGE) {
            gameWinner = side;
        } else if (opponentPoints == GamePoint.ADVANTAGE) {
            setScoreFor(side.opposite(), GamePoint.FORTY);
        } else if (winnerPoints == GamePoint.FORTY) {
            if (opponentPoints == GamePoint.FORTY) {
                setScoreFor(side, GamePoint.ADVANTAGE);
            } else {
                gameWinner = side;
            }
        } else {
            setScoreFor(side, winnerPoints.next());
        }
    }

    public Optional<PlayerSide> getWinner() {
        return Optional.ofNullable(gameWinner);
    }

    GamePoint getScoreFor(PlayerSide side) {
        return side == PlayerSide.FIRST ? firstSidePoints : secondSidePoints;
    }

    private void setScoreFor(PlayerSide side, GamePoint points) {
        if (side == PlayerSide.FIRST) firstSidePoints = points;
        else secondSidePoints = points;
    }
}
