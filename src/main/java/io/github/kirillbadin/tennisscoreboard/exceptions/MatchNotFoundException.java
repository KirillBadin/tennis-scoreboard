package io.github.kirillbadin.tennisscoreboard.exceptions;

import org.springframework.http.HttpStatus;


public class MatchNotFoundException extends ApiException {
    public MatchNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
