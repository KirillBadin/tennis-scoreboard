package io.github.kirillbadin.tennisscoreboard.live;

import lombok.Getter;

import java.util.Optional;
import java.util.UUID;

public class LiveMatch {
    @Getter
    private final UUID uuid;
    private final String firstPlayerName;
    private final String secondPlayerName;
    private LiveSet liveSet;
    private int firstSet;
    private int secondSet;

    public LiveMatch(String firstPlayerName, String secondPlayerName) {
        this.firstPlayerName = firstPlayerName;
        this.secondPlayerName = secondPlayerName;
        uuid = UUID.randomUUID();
        liveSet = new LiveSet();
    }

    public void addPoint(String name) {
        PlayerSide side = firstPlayerName.equals(name) ? PlayerSide.FIRST : PlayerSide.SECOND;
        liveSet.addPoint(side);
        liveSet.getWinner().ifPresent(this::handleSetWinner);
    }

    private void handleSetWinner(PlayerSide winnerSide) {
        if (winnerSide == PlayerSide.FIRST) {
            firstSet++;
        } else secondSet++;
        liveSet = new LiveSet();
    }

    public Optional<String> getWinnerName() {
        if (firstSet == 2) {
            return Optional.of(firstPlayerName);
        }
        if (secondSet == 2) {
            return Optional.of(secondPlayerName);
        }
        return Optional.empty();
    }

    public String getPlayerName(PlayerSide side) {
        return side == PlayerSide.FIRST ? firstPlayerName : secondPlayerName;
    }

    public int getSets(PlayerSide side) {
        return side == PlayerSide.FIRST ? firstSet : secondSet;
    }

    public int getGames(PlayerSide side) {
        return liveSet.getGames(side);
    }

    public int getPoints(PlayerSide side) {
        return liveSet.getPoints(side);
    }

    public Integer getTieBreakPoints(PlayerSide side) {
        return liveSet.getTieBreakPoints(side);
    }
}
