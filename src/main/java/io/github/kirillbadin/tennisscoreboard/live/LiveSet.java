package io.github.kirillbadin.tennisscoreboard.live;

import java.util.Optional;

public class LiveSet {
    private final static int WINNING_SET_POINTS = 6;
    private LivePoint livePoints;
    private int firstGame;
    private int secondGame;
    private boolean isTiebreak;


    public LiveSet() {
        livePoints =  LivePoint.forGame();
    }

    public void addPoint(PlayerSide playerSide) {
        livePoints.addPoint(playerSide);
        livePoints.getWinner().ifPresent(this::handleGameWin);
    }

    private void handleGameWin(PlayerSide winnerSide) {
        if (winnerSide == PlayerSide.FIRST) {
            firstGame++;
        } else secondGame++;

        if (firstGame == 6 && secondGame == 6) isTiebreak = true;
        newLivePoints(isTiebreak);
    }

    private void newLivePoints(boolean isTiebreak){
        if(isTiebreak){
            livePoints = LivePoint.forTiebreak();
        } else livePoints = LivePoint.forGame();
    }

    public Optional<PlayerSide> getWinner(){
        if (firstGame >= WINNING_SET_POINTS && firstGame - secondGame >= 2 || firstGame == 7) {
            return Optional.of(PlayerSide.FIRST);
        }
        if (secondGame >= WINNING_SET_POINTS && secondGame - firstGame >= 2 || secondGame == 7) {
            return Optional.of(PlayerSide.SECOND);
        }
        return Optional.empty();
    }

    public int getGames(PlayerSide side) {
        return side == PlayerSide.FIRST ? firstGame : secondGame;
    }

    public int getPoints(PlayerSide side) {
        return livePoints.getPoints(side);
    }

    public Integer getTieBreakPoints(PlayerSide side) {
        return isTiebreak ? livePoints.getPoints(side) : null;
    }
}
