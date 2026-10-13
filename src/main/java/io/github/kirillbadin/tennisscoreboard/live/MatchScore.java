package io.github.kirillbadin.tennisscoreboard.live;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MatchScore {
    private static final int SETS_TO_WIN = 2;

    private final List<SetScore> sets = new ArrayList<>();

    public MatchScore() {
        sets.add(new SetScore());
    }

    public void addPoint(PlayerSide side) {
        if (isFinished()) {
            throw new IllegalStateException("Match already finished");
        }
        SetScore currentSet = getCurrentSet();
        currentSet.addPoint(side);
        if (currentSet.isFinished() && !isFinished()) {
            sets.add(new SetScore());
        }
    }

    public Optional<PlayerSide> getWinner() {
        if (getSets(PlayerSide.FIRST) >= SETS_TO_WIN) {
            return Optional.of(PlayerSide.FIRST);
        }
        if (getSets(PlayerSide.SECOND) >= SETS_TO_WIN) {
            return Optional.of(PlayerSide.SECOND);
        }
        return Optional.empty();
    }

    public boolean isFinished() {
        return getWinner().isPresent();
    }

    public int getSets(PlayerSide side) {
        return (int) sets.stream()
                .filter(set -> isWonBy(set, side))
                .count();
    }

    public int getGames(PlayerSide side) {
        return getCurrentSet().getGames(side);
    }

    public GamePoint getGamePoint(PlayerSide side) {
        return getCurrentSet().getGamePoint(side);
    }

    public Integer getTieBreakPoints(PlayerSide side) {
        return getCurrentSet().getTieBreakPoints(side);
    }

    private SetScore getCurrentSet() {
        return sets.getLast();
    }

    private boolean isWonBy(SetScore set, PlayerSide side) {
        return set.getWinner().map(w -> w == side).orElse(false);
    }
}