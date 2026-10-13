package io.github.kirillbadin.tennisscoreboard.exceptions;

public class PlayerNotInMatchException extends RuntimeException {
    public PlayerNotInMatchException(String message) {
        super(message);
    }
}
