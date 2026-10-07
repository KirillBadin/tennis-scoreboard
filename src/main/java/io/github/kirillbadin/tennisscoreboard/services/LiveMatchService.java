package io.github.kirillbadin.tennisscoreboard.services;

import io.github.kirillbadin.tennisscoreboard.dto.*;
import io.github.kirillbadin.tennisscoreboard.live.LiveMatch;
import io.github.kirillbadin.tennisscoreboard.live.LiveMatchStorage;
import io.github.kirillbadin.tennisscoreboard.live.PlayerSide;
import io.github.kirillbadin.tennisscoreboard.repositories.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LiveMatchService {

    private final MatchRepository matchRepository;
    private final LiveMatchStorage liveMatchStorage;

    public CreateMatchResponseDto createNewMatch(CreateMatchRequestDto dto) {
        LiveMatch liveMatch = new LiveMatch(dto.firstPlayerName(), dto.secondPlayerName());
        liveMatchStorage.save(liveMatch);
        return new CreateMatchResponseDto(liveMatch.getUuid());
    }

    public MatchStateDto addPoint(UUID uuid, PointRequestDto pointRequestDto) {
        LiveMatch liveMatch = liveMatchStorage.getMatch(uuid); //TODO Проверка на uuid, чтоб совпал
        // TODO Проверка на имя игрока, чтоб он был в матче. и в liveMatch
        liveMatch.addPoint(pointRequestDto.name());
        return toMatchStateDto(liveMatch);
    }

    public MatchStateDto getScore(UUID uuid) {
        LiveMatch liveMatch = liveMatchStorage.getMatch(uuid);
        return toMatchStateDto(liveMatch);
    }
    private MatchStateDto toMatchStateDto(LiveMatch liveMatch) {
        PlayerStateDto firstPlayer = toPlayerState(liveMatch, PlayerSide.FIRST);
        PlayerStateDto secondPlayer = toPlayerState(liveMatch, PlayerSide.SECOND);
        String winnerName = liveMatch.getWinnerName().orElse(null);
        return new MatchStateDto(firstPlayer, secondPlayer, winnerName);
    }


    private PlayerStateDto toPlayerState(LiveMatch match, PlayerSide side) {
        int playerPoints = match.getPoints(side);
        int opponentPoints = match.getPoints(side.opposite());
        return new PlayerStateDto(
                match.getPlayerName(side),
                toDisplayPoints(playerPoints, opponentPoints),
                match.getGames(side),
                match.getSets(side),
                match.getTieBreakPoints(side)
        );
    }

    private String toDisplayPoints(int playerPoints, int opponentPoints) {
        if (playerPoints >= 3 && opponentPoints >= 3){
            if (playerPoints - opponentPoints == 1) return "AD";
            else return "40";
        }
        return switch (playerPoints) {
            case 0 -> "0";
            case 1 -> "15";
            case 2 -> "30";
            case 3 -> "40";
            default -> "";
        };
    }
}
