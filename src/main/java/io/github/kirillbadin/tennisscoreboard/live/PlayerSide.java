package io.github.kirillbadin.tennisscoreboard.live;

public enum PlayerSide {
    FIRST,
    SECOND;

    public PlayerSide opposite() {
        return this == FIRST ? SECOND : FIRST;
    }
}
