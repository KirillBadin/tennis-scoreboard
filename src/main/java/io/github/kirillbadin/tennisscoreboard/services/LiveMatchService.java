package io.github.kirillbadin.tennisscoreboard.services;

import io.github.kirillbadin.tennisscoreboard.dto.*;
import io.github.kirillbadin.tennisscoreboard.exceptions.MatchNotFoundException;
import io.github.kirillbadin.tennisscoreboard.exceptions.PlayerNamesConflictException;
import io.github.kirillbadin.tennisscoreboard.live.LiveMatch;
import io.github.kirillbadin.tennisscoreboard.live.LiveMatchStorage;
import io.github.kirillbadin.tennisscoreboard.live.PlayerSide;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LiveMatchService {

    private final LiveMatchStorage liveMatchStorage;
    private final MatchService matchService;
    private final PlayerService playerService;

    public CreateMatchResponseDto createNewMatch(CreateMatchRequestDto dto) {
        String firstPlayerName = dto.firstPlayerName().trim();
        String secondPlayerName = dto.secondPlayerName().trim();

        if (Objects.equals(firstPlayerName, secondPlayerName)) {
            throw new PlayerNamesConflictException("Имена игроков не могут совпадать");
        }

        playerService.getOrCreatePlayer(firstPlayerName);
        playerService.getOrCreatePlayer(secondPlayerName);

        LiveMatch liveMatch = new LiveMatch(firstPlayerName, secondPlayerName);
        liveMatchStorage.save(liveMatch);
        return new CreateMatchResponseDto(liveMatch.getUuid());
    }

    public MatchStateDto getScore(UUID uuid) {
        return toMatchStateDto(getMatch(uuid));
    }


    public MatchStateDto addPoint(UUID uuid, PointRequestDto pointRequestDto) {
        LiveMatch liveMatch = getMatch(uuid);
        synchronized (liveMatch) {
            liveMatch.addPoint(pointRequestDto.name());
            handleMatchCompletion(liveMatch);
            return toMatchStateDto(liveMatch);
        }
    }

    private LiveMatch getMatch(UUID uuid){
        return liveMatchStorage.getMatch(uuid)
                .orElseThrow(() -> new MatchNotFoundException("Матч с таким uuid не найден"));
    }

    private void handleMatchCompletion(LiveMatch liveMatch) {
        if (liveMatch.getWinnerSide().isEmpty()) {
            return;
        }
        matchService.save(liveMatch);
        liveMatchStorage.remove(liveMatch.getUuid());
    }

    private MatchStateDto toMatchStateDto(LiveMatch liveMatch) {
        PlayerStateDto firstPlayer = toPlayerState(liveMatch, PlayerSide.FIRST);
        PlayerStateDto secondPlayer = toPlayerState(liveMatch, PlayerSide.SECOND);
        String winnerName = liveMatch.getWinnerName().orElse(null);
        return new MatchStateDto(firstPlayer, secondPlayer, winnerName);
    }

    private PlayerStateDto toPlayerState(LiveMatch match, PlayerSide side) {
        Integer tieBreakPoints = match.getTieBreakPoints(side);
        boolean inTieBreak = tieBreakPoints != null;
        return new PlayerStateDto(
                match.getPlayerName(side),
                inTieBreak ? null : match.getGamePoint(side).getDisplay(),
                match.getGames(side),
                match.getSets(side),
                tieBreakPoints
        );
    }
}
