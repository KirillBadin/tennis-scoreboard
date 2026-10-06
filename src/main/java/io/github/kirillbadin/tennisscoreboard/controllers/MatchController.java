package io.github.kirillbadin.tennisscoreboard.controllers;

import io.github.kirillbadin.tennisscoreboard.dto.CreateMatchRequestDto;
import io.github.kirillbadin.tennisscoreboard.dto.CreateMatchResponseDto;
import io.github.kirillbadin.tennisscoreboard.dto.MatchesPageDto;
import io.github.kirillbadin.tennisscoreboard.services.LiveMatchService;
import io.github.kirillbadin.tennisscoreboard.services.MatchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/matches")
@Validated
public class MatchController {

    private final MatchService matchService;
    private final LiveMatchService liveMatchService;

    public MatchController(MatchService matchService, LiveMatchService liveMatchService) {
        this.matchService = matchService;
        this.liveMatchService = liveMatchService;
    }

    @GetMapping
    public ResponseEntity<MatchesPageDto> getMatches(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(name = "player_name", required = false) String playerName){

        MatchesPageDto matchesPageDto = matchService.getMatchesPage(pageable, playerName);
        return ResponseEntity.ok(matchesPageDto);
    }
    @PostMapping
    public ResponseEntity<CreateMatchResponseDto> createMatch(@Valid @RequestBody CreateMatchRequestDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(liveMatchService.createNewMatch(dto));
    }
}
