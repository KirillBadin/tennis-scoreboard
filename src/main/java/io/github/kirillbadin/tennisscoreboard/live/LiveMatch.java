package io.github.kirillbadin.tennisscoreboard.live;

import io.github.kirillbadin.tennisscoreboard.exceptions.PlayerNotInMatchException;
import lombok.Getter;

import java.util.Optional;
import java.util.UUID;

public class LiveMatch {
    @Getter
    private final UUID uuid;
    private final String firstPlayerName;
    private final String secondPlayerName;
    private final MatchScore matchScore;

    public LiveMatch(String firstPlayerName, String secondPlayerName) {
        this.firstPlayerName = firstPlayerName;
        this.secondPlayerName = secondPlayerName;
        this.uuid = UUID.randomUUID();
        this.matchScore = new MatchScore();
    }

    public void addPoint(String name) {
        if (firstPlayerName.equals(name)) {
            matchScore.addPoint(PlayerSide.FIRST);
            return;
        }
        if (secondPlayerName.equals(name)) {
            matchScore.addPoint(PlayerSide.SECOND);
            return;
        }
        throw new PlayerNotInMatchException("Игрок '" + name + "' не найден в матче");
    }

    public Optional<String> getWinnerName() {
        return matchScore.getWinner().map(this::getPlayerName);
    }

    public String getPlayerName(PlayerSide side) {
        return side == PlayerSide.FIRST ? firstPlayerName : secondPlayerName;
    }

    public int getSets(PlayerSide side) {
        return matchScore.getSets(side);
    }

    public int getGames(PlayerSide side) {
        return matchScore.getGames(side);
    }

    public GamePoint getGamePoint(PlayerSide side) {
        return matchScore.getGamePoint(side);
    }

    public Integer getTieBreakPoints(PlayerSide side) {
        return matchScore.getTieBreakPoints(side);
    }
}