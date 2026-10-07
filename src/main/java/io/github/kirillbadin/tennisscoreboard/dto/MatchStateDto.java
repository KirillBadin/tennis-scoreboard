package io.github.kirillbadin.tennisscoreboard.dto;

public record MatchStateDto (PlayerStateDto firstPlayer, PlayerStateDto secondPlayer, String winnerName){
}
