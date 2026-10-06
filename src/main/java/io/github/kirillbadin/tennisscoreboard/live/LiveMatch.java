package io.github.kirillbadin.tennisscoreboard.live;

import lombok.Getter;

import java.util.UUID;

public class LiveMatch {
    @Getter
    private final UUID uuid;
    private final String firstPlayerName;
    private final String secondPlayerName;
    private String winner;


    public LiveMatch(String firstPlayerName, String secondPlayerName) {
        this.firstPlayerName = firstPlayerName;
        this.secondPlayerName = secondPlayerName;
        uuid = UUID.randomUUID();
    }

}
