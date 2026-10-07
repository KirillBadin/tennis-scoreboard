package io.github.kirillbadin.tennisscoreboard.live;

import java.util.Optional;

public class LivePoint {
    private final int winningPoints;
    private int firstPoints;
    private int secondPoints;

    private LivePoint(int winningPoints) {
        this.winningPoints = winningPoints;
    }

    public static LivePoint forGame(){return new LivePoint(4); }
    public static LivePoint forTiebreak(){return new LivePoint(7); }

    public void addPoint(PlayerSide playerSide) {
        if(playerSide == PlayerSide.FIRST){
            firstPoints++;
        } else {
            secondPoints++;
        }
    }

    public Optional<PlayerSide> getWinner(){
        if (firstPoints >= winningPoints && firstPoints - secondPoints >= 2) {
            return Optional.of(PlayerSide.FIRST);
        }
        if (secondPoints >= winningPoints && secondPoints - firstPoints >= 2) {
            return Optional.of(PlayerSide.SECOND);
        }
        return Optional.empty();
    }

    public int getPoints(PlayerSide side) {
        return side == PlayerSide.FIRST ? firstPoints : secondPoints;
    }
}
