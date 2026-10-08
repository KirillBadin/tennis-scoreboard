package io.github.kirillbadin.tennisscoreboard.exceptions;

import org.springframework.http.HttpStatus;

public class PlayerNamesConflictException extends ApiException {
    public PlayerNamesConflictException(String message) {
        super(message,HttpStatus.BAD_REQUEST);
    }
}
