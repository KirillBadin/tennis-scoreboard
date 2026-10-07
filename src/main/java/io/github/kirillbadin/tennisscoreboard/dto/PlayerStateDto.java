package io.github.kirillbadin.tennisscoreboard.dto;

public record PlayerStateDto (String name, String points, int games, int sets, Integer tieBreakPoints) {
}
