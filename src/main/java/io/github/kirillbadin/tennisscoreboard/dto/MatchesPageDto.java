package io.github.kirillbadin.tennisscoreboard.dto;

import java.util.List;

public record MatchesPageDto(List<MatchDto> matches,
                             int currentPage,
                             int totalPages) {
}
