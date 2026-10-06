package io.github.kirillbadin.tennisscoreboard.controllers;

import io.github.kirillbadin.tennisscoreboard.dto.MatchesPageDto;
import io.github.kirillbadin.tennisscoreboard.services.MatchService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public ResponseEntity<MatchesPageDto> getMatches(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(name = "player_name", required = false) String playerName){

        MatchesPageDto matchesPageDto = matchService.getMatchesPage(pageable, playerName);
        return ResponseEntity.ok(matchesPageDto);
    }
}
