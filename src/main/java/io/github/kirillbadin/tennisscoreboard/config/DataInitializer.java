package io.github.kirillbadin.tennisscoreboard.config;

import io.github.kirillbadin.tennisscoreboard.entity.Match;
import io.github.kirillbadin.tennisscoreboard.entity.Player;
import io.github.kirillbadin.tennisscoreboard.repositories.MatchRepository;
import io.github.kirillbadin.tennisscoreboard.repositories.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PlayerRepository playerRepository;
    private final MatchRepository matchRepository;

    @Override
    public void run(String... args) {
        if (playerRepository.count() == 0){
           playerRepository.saveAll(
                   List.of(
                           new Player("Rafael Nadal"),
                           new Player("Roger Federer"),
                           new Player("Novak Djokovic"),
                           new Player("Andy Murray"),
                           new Player("Daniil Medvedev")
                   ));
        }
        if(matchRepository.count() == 0){
            Player firstPlayer = playerRepository.findByName("Rafael Nadal")
                    .orElseThrow(() -> new IllegalStateException("Nadal not found"));
            Player secondPlayer = playerRepository.findByName("Roger Federer")
                    .orElseThrow(() -> new IllegalStateException("Federer not found"));
            Player thirdPlayer = playerRepository.findByName("Novak Djokovic")
                    .orElseThrow(()-> new IllegalStateException("Djokovic not found"));

            matchRepository.saveAll( List.of(
                    new Match(firstPlayer, secondPlayer, firstPlayer),
                    new Match(thirdPlayer, secondPlayer, secondPlayer),
                    new Match(firstPlayer, thirdPlayer, thirdPlayer)
            ));
        }
    }
}
