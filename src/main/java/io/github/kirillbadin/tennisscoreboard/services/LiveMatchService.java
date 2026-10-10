package io.github.kirillbadin.tennisscoreboard.services;

import io.github.kirillbadin.tennisscoreboard.dto.*;
import io.github.kirillbadin.tennisscoreboard.exceptions.MatchNotFoundException;
import io.github.kirillbadin.tennisscoreboard.exceptions.PlayerNamesConflictException;
import io.github.kirillbadin.tennisscoreboard.live.LiveMatch;
import io.github.kirillbadin.tennisscoreboard.live.LiveMatchStorage;
import io.github.kirillbadin.tennisscoreboard.live.PlayerSide;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LiveMatchService {

    private final LiveMatchStorage liveMatchStorage;
    private final MatchService matchService;

    public CreateMatchResponseDto createNewMatch(CreateMatchRequestDto dto) {
        if(dto.firstPlayerName().equals(dto.secondPlayerName())){
            throw new PlayerNamesConflictException("Имена игроков не могут совпадать");
        }

        LiveMatch liveMatch = new LiveMatch(dto.firstPlayerName(), dto.secondPlayerName());
        liveMatchStorage.save(liveMatch);
        return new CreateMatchResponseDto(liveMatch.getUuid());
    }

    public MatchStateDto getScore(UUID uuid) {
        return toMatchStateDto(getMatch(uuid));
    }


    public MatchStateDto addPoint(UUID uuid, PointRequestDto pointRequestDto) {
        LiveMatch liveMatch = getMatch(uuid);
        // TODO Проверка на имя игрока, чтоб он был в матче. и в liveMatch
        liveMatch.addPoint(pointRequestDto.name());
        handleMatchCompletion(liveMatch);
        return toMatchStateDto(liveMatch);
    }

    public LiveMatch getMatch(UUID uuid){
        return liveMatchStorage.getMatch(uuid)
                .orElseThrow(() -> new MatchNotFoundException("матч с таким uuid не найден"));
    }

    private void handleMatchCompletion(LiveMatch liveMatch) {
        liveMatch.getWinnerName().ifPresent(winnerName -> {
           matchService.save(liveMatch);
           liveMatchStorage.remove(liveMatch.getUuid());
        });
    }

    private MatchStateDto toMatchStateDto(LiveMatch liveMatch) {
        PlayerStateDto firstPlayer = toPlayerState(liveMatch, PlayerSide.FIRST);
        PlayerStateDto secondPlayer = toPlayerState(liveMatch, PlayerSide.SECOND);
        String winnerName = liveMatch.getWinnerName().orElse(null);
        return new MatchStateDto(firstPlayer, secondPlayer, winnerName);
    }

    private PlayerStateDto toPlayerState(LiveMatch match, PlayerSide side) {
        Integer tieBreakPoints = match.getTieBreakPoints(side);
        String points = (tieBreakPoints == null)
                ? toDisplayPoints(match.getPoints(side), match.getPoints(side.opposite()))
                : null;
        return new PlayerStateDto(
                match.getPlayerName(side),
                points,
                match.getGames(side),
                match.getSets(side),
                tieBreakPoints
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
