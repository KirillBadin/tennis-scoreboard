package io.github.kirillbadin.tennisscoreboard.controllers;

import io.github.kirillbadin.tennisscoreboard.dto.*;
import io.github.kirillbadin.tennisscoreboard.services.LiveMatchService;
import io.github.kirillbadin.tennisscoreboard.services.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/matches")
public class MatchController {

    private static final int DEFAULT_PAGE_SIZE = 2;
    private final MatchService matchService;
    private final LiveMatchService liveMatchService;


    @GetMapping
    public ResponseEntity<MatchesPageDto> getMatches(
            @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = "id") Pageable pageable,
            @RequestParam(name = "player_name", required = false) String playerName){
        return ResponseEntity.ok(matchService.getMatchesPage(pageable, playerName));
    }

    @PostMapping
    public ResponseEntity<CreateMatchResponseDto> createMatch(@Valid @RequestBody CreateMatchRequestDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(liveMatchService.createNewMatch(dto));
    }

    @PostMapping("/{uuid}/point")
    public ResponseEntity<MatchStateDto> addPoint(@PathVariable UUID uuid,
                                                  @Valid @RequestBody PointRequestDto pointRequestDto){
        MatchStateDto matchStateDto = liveMatchService.addPoint(uuid, pointRequestDto);
        return ResponseEntity.ok(matchStateDto);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<MatchStateDto> getScore(@PathVariable UUID uuid){
        return ResponseEntity.ok(liveMatchService.getScore(uuid));
    }
}
