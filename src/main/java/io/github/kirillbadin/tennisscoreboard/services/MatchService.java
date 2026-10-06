package io.github.kirillbadin.tennisscoreboard.services;

import io.github.kirillbadin.tennisscoreboard.dto.MatchDto;
import io.github.kirillbadin.tennisscoreboard.dto.MatchesPageDto;
import io.github.kirillbadin.tennisscoreboard.entity.Match;
import io.github.kirillbadin.tennisscoreboard.repositories.MatchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MatchService {

    private final MatchRepository matchRepository;

    @Autowired
    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public MatchesPageDto getMatchesPage(Pageable pageable, String playerName) {
        Page<Match> allMatches = (playerName == null || playerName.isBlank())
                ? matchRepository.findAll(pageable)
                : matchRepository.findAllByPlayerName(playerName, pageable);

        Page<MatchDto> dtoPage = allMatches.map(match -> new MatchDto(
                                                        match.getFirstPlayer().getName(),
                                                        match.getSecondPlayer().getName(),
                                                        match.getWinner().getName()
        ));
        return new MatchesPageDto(
                dtoPage.getContent(),
                dtoPage.getNumber()+1,  // Spring 0-based → фронт 1-based
                dtoPage.getTotalPages());
    }
}
