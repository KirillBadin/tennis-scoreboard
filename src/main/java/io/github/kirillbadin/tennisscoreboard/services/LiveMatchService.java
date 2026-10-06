package io.github.kirillbadin.tennisscoreboard.services;

import io.github.kirillbadin.tennisscoreboard.dto.CreateMatchRequestDto;
import io.github.kirillbadin.tennisscoreboard.dto.CreateMatchResponseDto;
import io.github.kirillbadin.tennisscoreboard.live.LiveMatch;
import io.github.kirillbadin.tennisscoreboard.live.LiveMatchStorage;
import io.github.kirillbadin.tennisscoreboard.repositories.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
