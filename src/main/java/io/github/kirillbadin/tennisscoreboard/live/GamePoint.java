package io.github.kirillbadin.tennisscoreboard.live;

public enum GamePoint {
    LOVE("0"),
    FIFTEEN("15"),
    THIRTY("30"),
    FORTY("40"),
    ADVANTAGE("AD");

    private final String display;

    GamePoint(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public GamePoint next() {
        if (this == ADVANTAGE) {
            throw new IllegalStateException("ADVANTAGE has no next value");
        }
        return values()[ordinal() + 1];
    }
}
